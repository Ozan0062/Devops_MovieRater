# MovieRater

React/Vite-app med ekstern Supabase.

## Forudsætninger

- Docker Desktop med Linux-containere, eller Docker på en Linux-server.
- Docker Compose 2.24.4 eller nyere.

Start Docker, og kør lokale kommandoer fra projektmappen.

## Supabase

Hvis du ikke allerede har en `.env`, kopiér eksempel-filen:

```sh
cp .env.example .env
```

Udfyld `.env` med dit Supabase-projekts oplysninger:

```dotenv
VITE_SUPABASE_URL=https://your-project.supabase.co
VITE_SUPABASE_PUBLISHABLE_KEY=sb_publishable_REPLACE_WITH_YOUR_PUBLIC_KEY
```

Commit ikke `.env`. Brug kun en offentlig publishable key; værdier med
`VITE_` kan læses i browseren. Brug aldrig secret keys eller databasepasswords.

Supabase er ekstern, og der startes ingen databasecontainer. Ændrer du disse
variabler, skal produktionsimaget bygges igen.

## Start lokalt

```sh
docker compose up --build -d
```

Åbn [http://localhost:8080](http://localhost:8080).

| Handling | Kommando |
| --- | --- |
| Status | `docker compose ps` |
| Healthcheck | `curl http://localhost:8080/health` |
| Logs | `docker compose logs -f` |
| Stop | `docker compose down` |

Containeren skal vise `healthy`, og healthcheck skal svare `OK`.
Brug `curl.exe` i Windows PowerShell. Kør startkommandoen igen efter kodeændringer.

## Development

Start Vite med hot reload:

```sh
docker compose -p movierater-dev -f docker-compose.dev.yml up --build -d
```

Åbn [http://localhost:5173](http://localhost:5173). Ændringer i `src/` vises
automatisk. Genkør med `--build` efter ændringer af dependencies.

Efter ændringer af `.env`:

```sh
docker compose -p movierater-dev -f docker-compose.dev.yml up -d --force-recreate
```

Stop development:

```sh
docker compose -p movierater-dev -f docker-compose.dev.yml down
```

## CI/CD

Opret disse repository variables på GitHub under
**Settings → Secrets and variables → Actions → Variables**:

- `VITE_SUPABASE_URL`
- `VITE_SUPABASE_PUBLISHABLE_KEY`

- **Pull request til main:** bygger app og Docker-image, starter en testcontainer
  og kontrollerer healthcheck, HTTP og sikkerhedsindstillinger.
- **Push/merge til main:** tester og publicerer imaget til
  `ghcr.io/ozan0062/devops_movierater` med `sha-<fuldt-commit-id>`, `main` og `latest`.
  Versionstags som `v1.2.3` understøttes også.
- **Scanning:** Trivy scanner image og dependencies. Fund gemmes som rapporter
  og er foreløbigt informative.
- Lint/tests køres kun, hvis projektet har scripts til dem. Det har det ikke endnu.

Publicering bruger `GITHUB_TOKEN`. Serveren opdateres manuelt.

## Deployment på server

Når et image er publiceret, kopiér `docker-compose.prod.yml` til en mappe på
serveren. Opret `.env.server` i samme mappe:

```dotenv
MOVIERATER_IMAGE=ghcr.io/ozan0062/devops_movierater:sha-REPLACE_WITH_FULL_COMMIT_SHA
HTTP_PORT=8080
```

Indsæt det fulde commit-id for den version, du vil køre. Hvis GHCR-pakken er
privat, log ind med `docker login ghcr.io` og et token med `read:packages`.

Hent, start og kontrollér:

```sh
docker compose -p movierater --env-file .env.server -f docker-compose.prod.yml pull
docker compose -p movierater --env-file .env.server -f docker-compose.prod.yml up -d --wait
docker compose -p movierater --env-file .env.server -f docker-compose.prod.yml ps
curl --fail http://127.0.0.1:8080/health
```

Port 8080 er kun tilgængelig lokalt på serveren. Serveren bruger det færdige
image og behøver ikke Node/npm eller appens source code.

Logs og stop:

```sh
docker compose -p movierater --env-file .env.server -f docker-compose.prod.yml logs -f
docker compose -p movierater --env-file .env.server -f docker-compose.prod.yml down
```

## HTTPS

Kopiér også `docker-compose.https.yml` og `Caddyfile` til serveren.
Peg dit domæne mod serverens IP, og åbn TCP-port 80 og 443.

Tilføj dit rigtige domæne og din email i `.env.server`:

```dotenv
MOVIERATER_DOMAIN=movierater.example.com
ACME_EMAIL=admin@example.com
```

Hent og start med begge Compose-filer:

```sh
docker compose -p movierater --env-file .env.server -f docker-compose.prod.yml -f docker-compose.https.yml pull
docker compose -p movierater --env-file .env.server -f docker-compose.prod.yml -f docker-compose.https.yml up -d --wait
curl --fail https://movierater.example.com/health
```

Caddy håndterer HTTPS og videresender trafik til MovieRater. Kun port 80/443
publiceres. Brug dit eget domæne i curl-kommandoen.

Brug begge `-f`-filer ved status, logs, opdatering og stop.
Brug `down` uden `-v`, så certifikaterne bevares.

## Opdatering og rollback

1. Gem den nuværende `MOVIERATER_IMAGE` som din rollback-version.
2. Vælg en ny version ved at ændre `MOVIERATER_IMAGE` i `.env.server`.
3. Kør deployment-kommandoerne `pull` og `up -d --wait` igen.
4. Kontrollér `ps`, healthcheck, hjemmesiden og logs.

For rollback sættes `MOVIERATER_IMAGE` tilbage til den tidligere commit-version,
og de samme kommandoer køres. Der kræves intet rebuild. Bevar tidligere images
i GHCR, og brug commit-tags eller digests til rollback.

## Vedligeholdelse

Opdatér base-image-versioner og digests via en PR. Hold Node-versionen ens i
Dockerfilerne og `.nvmrc`, og gennemgå CI-kontrollerne og Trivy-rapporten.
Den midlertidige `pcre2`-pin i Dockerfile kan fjernes, når base-imaget
indeholder sikkerhedsrettelsen.
