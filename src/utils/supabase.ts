import { createClient } from '@supabase/supabase-js';

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL?.trim();
const supabaseKey = import.meta.env.VITE_SUPABASE_PUBLISHABLE_KEY?.trim();

if (!supabaseUrl || !supabaseKey) {
  throw new Error(
    'Supabase-konfiguration mangler. Udfyld VITE_SUPABASE_URL og ' +
      'VITE_SUPABASE_PUBLISHABLE_KEY i .env, og genstart eller genbyg appen.',
  );
}

export const supabase = createClient(supabaseUrl, supabaseKey);
