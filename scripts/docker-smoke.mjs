import assert from 'node:assert/strict';
import { spawnSync } from 'node:child_process';
import { randomUUID } from 'node:crypto';
import { setTimeout as sleep } from 'node:timers/promises';

// Run against a built image, on Windows or Linux: node scripts/docker-smoke.mjs IMAGE
const image = process.argv[2];
if (!image) {
  console.error('Usage: node scripts/docker-smoke.mjs IMAGE');
  process.exit(1);
}

const name = process.env.DOCKER_SMOKE_CONTAINER || `movierater-smoke-${randomUUID()}`;
let containerId;

function docker(args, allowFailure = false) {
  const result = spawnSync('docker', args, {
    encoding: 'utf8',
    timeout: 30_000,
    maxBuffer: 5 * 1024 * 1024,
  });
  if (!allowFailure && (result.error || result.status !== 0)) {
    throw new Error(`docker ${args[0]} failed: ${result.error?.message || result.stderr || result.stdout}`);
  }
  return result;
}

function inspect() {
  return JSON.parse(docker(['inspect', containerId]).stdout)[0];
}

async function request(base, path) {
  return fetch(`${base}${path}`, { signal: AbortSignal.timeout(5000) });
}

function securityHeaders(response) {
  assert.equal(response.headers.get('x-content-type-options'), 'nosniff');
  assert.equal(response.headers.get('referrer-policy'), 'strict-origin-when-cross-origin');
}

try {
  const imageConfig = JSON.parse(docker(['image', 'inspect', image]).stdout)[0].Config;
  assert(imageConfig.Healthcheck?.Test?.[0] !== 'NONE' && imageConfig.Healthcheck?.Test?.length,
    'Image must define its own HTTP healthcheck');
  assert(imageConfig.User && !['0', 'root', '0:0', 'root:root'].includes(imageConfig.User),
    'Image must set a non-root USER');

  containerId = docker([
    'run', '--detach', '--name', name,
    '--publish', '127.0.0.1::8080',
    '--read-only', '--tmpfs', '/tmp:rw,noexec,nosuid,size=32m',
    '--cap-drop', 'ALL', '--security-opt', 'no-new-privileges:true',
    '--memory', '256m', '--cpus', '0.5',
    '--health-interval', '2s', '--health-timeout', '3s',
    '--health-retries', '10', '--health-start-period', '1s',
    image,
  ]).stdout.trim();

  const deadline = Date.now() + 90_000;
  let details;
  while (Date.now() < deadline) {
    details = inspect();
    assert(details.State.Running, `Container stopped (exit ${details.State.ExitCode})`);
    if (details.State.Health?.Status === 'healthy') break;
    if (details.State.Health?.Status === 'unhealthy') throw new Error('Container healthcheck is unhealthy');
    await sleep(1000);
  }
  assert.equal(details.State.Health?.Status, 'healthy', 'Timed out waiting for a healthy container');
  assert.equal(details.RestartCount, 0, 'Container must not restart during startup');
  assert.equal(details.HostConfig.ReadonlyRootfs, true);
  assert(details.HostConfig.CapDrop.includes('ALL'));
  assert(details.HostConfig.SecurityOpt.some((option) => option.startsWith('no-new-privileges')));
  const uid = docker(['exec', containerId, 'id', '-u']).stdout.trim();
  assert.notEqual(uid, '0', 'Runtime must run as a non-root user');
  const status = docker(['exec', containerId, 'cat', '/proc/1/status']).stdout;
  assert.match(status, /^CapEff:\s+0+$/m, 'Nginx must not have effective Linux capabilities');
  assert.match(status, /^NoNewPrivs:\s+1$/m);
  assert.equal(docker(['exec', containerId, 'sh', '-c',
    'if command -v node >/dev/null 2>&1 || command -v npm >/dev/null 2>&1; then exit 1; fi'], true).status,
    0, 'Runtime image must not contain Node or npm');

  const port = details.NetworkSettings.Ports['8080/tcp'][0].HostPort;
  const base = `http://127.0.0.1:${port}`;
  const health = await request(base, '/health');
  assert.equal(health.status, 200);
  assert.equal((await health.text()).trim(), 'OK');
  securityHeaders(health);

  const home = await request(base, '/');
  assert.equal(home.status, 200);
  assert.match(home.headers.get('content-type'), /text\/html/);
  assert.match(home.headers.get('cache-control') || '', /no-cache|no-store/);
  securityHeaders(home);
  const html = await home.text();
  assert.match(html, /<div[^>]+id=["']root["']/);

  // This tests Nginx's SPA fallback even when the app has no React Router routes yet.
  const route = await request(base, '/__docker_spa_check__/movie/123');
  assert.equal(route.status, 200);
  assert.equal(await route.text(), html);
  securityHeaders(route);

  const assets = [...html.matchAll(/(?:src|href)=["'](\/assets\/[^"']+)["']/g)]
    .map((match) => match[1]);
  assert(assets.some((asset) => asset.endsWith('.js')), 'Built HTML must reference JavaScript');
  let gzipVerified = false;
  for (const asset of new Set(assets)) {
    const response = await fetch(`${base}${asset}`, {
      headers: { 'Accept-Encoding': 'gzip' },
      signal: AbortSignal.timeout(5000),
    });
    assert.equal(response.status, 200, `Asset failed: ${asset}`);
    assert.match(response.headers.get('cache-control') || '', /immutable/);
    assert.match(response.headers.get('cache-control') || '', /max-age=31536000/);
    securityHeaders(response);
    if (asset.endsWith('.js')) assert.match(response.headers.get('content-type'), /javascript/);
    if (asset.endsWith('.css')) assert.match(response.headers.get('content-type'), /text\/css/);
    const bytes = (await response.arrayBuffer()).byteLength;
    assert(bytes > 0, `Asset is empty: ${asset}`);
    if (asset.endsWith('.js') && bytes > 1024) {
      assert.equal(response.headers.get('content-encoding'), 'gzip', 'JavaScript should be compressed');
      gzipVerified = true;
    }
  }
  assert(gzipVerified, 'At least one JavaScript bundle should use gzip');
  assert.equal((await request(base, '/assets/__missing__.js')).status, 404);
  assert.equal(inspect().RestartCount, 0);
  const logs = docker(['logs', '--tail', '100', containerId]).stderr;
  assert.doesNotMatch(logs, /\[(?:emerg|alert|crit)\]/i, 'Nginx reported a serious error');
  console.log(`PASS: healthy container, UID ${uid}, hardened runtime, HTTP, assets, SPA fallback and headers (${base})`);
} catch (error) {
  console.error(`FAIL: ${error.message}`);
  if (containerId) {
    const logs = docker(['logs', '--tail', '100', containerId], true);
    console.error(logs.stdout, logs.stderr);
  }
  process.exitCode = 1;
} finally {
  if (containerId) {
    const cleanup = docker(['rm', '--force', containerId], true);
    if (cleanup.status !== 0) {
      console.error(`Container cleanup failed: ${cleanup.stderr || cleanup.error?.message}`);
      process.exitCode = 1;
    }
  }
}
