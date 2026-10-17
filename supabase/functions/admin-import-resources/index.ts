// Retired. This was a one-shot importer used to load the event seed file into
// public.resources on 2026-10-17. It is disabled so it cannot be used as a write path.
// To re-seed the database, apply supabase/seed.sql (see the README).
import "jsr:@supabase/functions-js/edge-runtime.d.ts";

Deno.serve(() =>
  new Response(JSON.stringify({ error: "gone: import is now supabase/seed.sql" }), {
    status: 410,
    headers: { "Content-Type": "application/json" },
  })
);
