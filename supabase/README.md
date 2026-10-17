# Supabase backend

Postgres is the authoring store for resource records and the human-reviewed copy
written about them. The Edge Function `api` serves the client.

There is **no Kotlin server**: `api/intake` holds the Anthropic key and replaces it.

## Routes

Base URL: `https://<project>.supabase.co/functions/v1/api`

| Route | Method | What it does |
|---|---|---|
| `/health` | GET | Liveness plus the configured model |
| `/bundle` | GET | One payload: resources + reviewed explanations. The client caches this and runs offline from then on |
| `/intake` | POST | Claude turns a typed sentence into `{need, city, constraints, route}`. Returns 503 when unavailable so the client falls back to picking a category |

## Tables

| Table | Who can read | Who can write |
|---|---|---|
| `resources` | anyone (`anon` select) | nobody via the API |
| `explanations` | anyone, **but only rows with `reviewed_at` set** | nobody via the API |
| `flags` | nobody via the API (grants revoked) | service role only |

Unreviewed model-written copy is invisible to clients by RLS policy, not by
convention. `api/bundle` deliberately reads with the anon key so that policy
applies server-side too.

## Setting the Anthropic key

`/intake` returns 503 until this is set. Nothing else is affected.

```bash
supabase secrets set ANTHROPIC_API_KEY=sk-ant-...   # optionally CLAUDE_MODEL
```

Or Dashboard → Edge Functions → Secrets.

## Re-seeding the database

Resource records are reference data and live in git as SQL.

```bash
python3 tools/generate-seed-sql.py   # only when the seed JSON changes
supabase db push                     # migrations + seed.sql
```

`supabase/seed.sql` is generated and round-trip verified against
`composeApp/src/commonMain/composeResources/files/oc-resources.seed.json` (40 records,
920 fields). Never hand-edit it, and never add a resource fact that isn't in a
source record.
