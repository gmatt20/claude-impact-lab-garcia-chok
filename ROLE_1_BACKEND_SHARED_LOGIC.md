# ROLE 1 — KOTLIN SHARED LOGIC & BACKEND ENGINEER

**Project:** Bridge | **Challenge 2** — Finding the right resource
**Read `BRIDGE_SHARED_CONTEXT.md` first.** The event repo's `CLAUDE.md` and `docs/ground-rules.md`
override both.

## YOUR MISSION

Own the server-side intelligence that interprets a client's described need, matches only the
event's verified resource records, and returns a simple, actionable next step.

> **Architecture changed on the day.** There is **no Kotlin/Ktor server**. The backend is
> Supabase: Postgres holds the resource records and human-reviewed copy, and a single Edge
> Function (`api`, TypeScript on Deno) serves `/api/bundle` and `/api/intake`. The Anthropic key
> lives as a Supabase secret and never reaches the client. See `supabase/README.md`.
>
> Matching currently runs **client-side** in `composeApp/.../domain/Matcher.kt`, which is what
> makes the offline path work. Do not move it to the server without agreeing that trade first.

## OWNERSHIP

- The Supabase Edge Function `api`: routes, validation, error handling, CORS.
- The database schema, RLS policies, and migrations under `supabase/`.
- Claude integration (**server only**), model configurable via `CLAUDE_MODEL`;
  default `claude-haiku-4-5`.
- Deterministic matching and eligibility filters over the event seed file.
- The safety router: danger, clinical, and unknown routes.
- Build-time generation of human-reviewed fit explanations.
- The build-time link and claim checker (ground rule 3: quality control).
- The 12-scenario eval harness.
- `GET /api/bundle` — the single cacheable offline payload.

## DO FIRST (FIRST 20 MINUTES)

1. Read the shared context. Inspect the repo and build setup; do not overwrite your partner's work.
2. Copy `challenges/2-finding-the-right-resource/data/oc-resources.seed.json` into the server's
   resources. **Do not build your own resource list** — see RESOURCE DATA below.
3. Agree API paths, JSON shapes, and DTO location with the Frontend Engineer. Settle the three open
   items at the end of the shared context's contract section.
4. Create the DTOs mirroring the seed schema and publish mock endpoint responses immediately.
5. Get one runnable request working before adding Claude.

## RESOURCE DATA — USE THE SEED FILE

`oc-resources.seed.json` gives us **40 entries**, each sourced from the organization's own page or a
government page, with a verbatim `source_quote` re-fetched and matched word-for-word on 2026-10-13.
Our old plan of hand-curating 8–15 resources is obsolete and would be strictly worse.

**DTOs mirror the seed schema exactly. Do not rename fields.** The field names are
`verified_date`, `source_quote`, `source_url`, `how_to_access`, `referral_required`, `confidence` —
not invented equivalents like `lastVerified` or `lastCheckedAt`.

```
id, name, organization, category, tags[], description, services[], eligibility,
population[], how_to_access, referral_required, phone, address, city, hours,
languages[], cost, website, source_url, source_quote, verified_date, confidence, notes
```

Categories are the 16 in the seed file (`shelter`, `employment`, `microenterprise`,
`family-reunification-travel`, `housing-navigation`, `id-documents`, `transportation`,
`phone-internet`, `benefits`, `food`, `health-coverage`, `behavioral-health`, `legal`, `veterans`,
`hygiene-mail-storage`, `other`). Those are the wire values.

Nullable fields are genuinely null in the data: **20 of 40 records have no `address`**, 3 have no
`phone`, and `referral_required` is null on 3. **Never invent a value to fill a gap.** Unknown is a
valid, required answer. `wiseplace` (women / survivors) already ships `address: null` — the seed
authors withheld it. Preserve that, and apply the same rule to anything we add ourselves.

Adding entries from `DATA-SOURCES.md` is optional and only if the seed journey is already complete.
**Do not scrape 211OC, OC Navigator, or findhelp.org** — findhelp's terms forbid it explicitly, and
211OC sets `Crawl-delay: 10` with no reuse terms. FindTreatment.gov has a working JSON endpoint if
you need one.

## MATCHING — THE LOCKED-DOOR PROBLEM IS THE WHOLE JOB

The event repo's bluntest warning: **"Most shelters are referral-only. A shelter address in your app
can send someone to a locked door."** Deterministic filtering must therefore respect access
mechanics, not just category:

- `referral_required` is true on 7 of 40 records. When true, the next step is **the referral
  route**, never "here's the address, walk in."
- `how_to_access` values: `mixed` (16), `phone` (7), `referral-only` (6), `online` (6),
  `walk-in` (4), `appointment` (1). Filter against the client's constraints:
  - **C08** has no phone → never return a `phone`-only resource as the primary action.
  - **C06** can't hear on the phone and asks to go see somebody → prefer `walk-in` / `appointment`.
  - **C01** uses a walker and has no ID → surface accessibility and ID requirements from
    `eligibility` and `notes`, don't hide them.
- `address` is not always the service site — several source records held a head office. Treat
  `address` as display-only context, never as routing.
- Match on `category` first, then `city` / service area, then `tags`, then constraints.
  Deterministic filtering first; AI wording second.
- Return no-match explicitly, with a safe alternative (211 / staff assistance).

## CLAUDE DESIGN

- Claude classifies the need, extracts a few practical constraints, and optionally composes one
  brief follow-up question. It is for **language understanding, not truth.**
- Use the official Java SDK from Kotlin: `implementation("com.anthropic:anthropic-java:2.34.0")`,
  client via `AnthropicOkHttpClient.fromEnv()`. **Do not hand-roll Ktor HTTP calls** against the
  Messages API.
- Model: `claude-haiku-4-5` for intake. Consider `claude-sonnet-5` only if extraction tests show a
  real need and time allows. (Note: `claude-haiku-4-5` rejects `output_config.effort`, and its
  thinking config uses the legacy `budget_tokens` form — for intake use neither, just fast
  structured output.)
- Structured output is `output_config: { format: {...} }` (the older `output_format` parameter is
  deprecated). Use `strict: true` on tool definitions. Validate against Kotlin DTOs, clamp length,
  reject invalid values.
- **Hard ~3s timeout.** On timeout, invalid JSON, or any API error, return the deterministic
  category-path result rather than an error. The frontend must never show a spinner that outlives
  the timeout.
- Prompt: empathetic, straightforward, never moralizing, one question at a time, no assumptions
  about identity or health, always invite skip.
- **Resource names, organizations, phone numbers, addresses, hours, eligibility, and availability
  always come from the seed record — never from model memory.**

### The safety router (`route` on the intake response)

| `route` | Trigger | Behavior |
|---|---|---|
| `danger` | Client indicates they are unsafe (**C10**: "He'll find me") | Return hotline + staff route. **Set `addressWithheld: true` and never emit a DV shelter address.** |
| `clinical` | Medical, legal, or immigration question (**C11**: "Can I just stop taking them?") | Refuse to advise. Route to a clinician or staff. `needs` may legitimately be empty. |
| `unknown` | No confident need, or no match in the data | Explicit no-match plus 211 / staff alternative. |
| `normal` | Everything else | Proceed to matching. |

Do not perform medical triage, infer diagnoses, or make clinical eligibility or placement
decisions. Nothing client-facing gives medical, legal, or immigration advice.

## QUALITY CONTROL — GROUND RULE 3, AND OUR SCORING EDGE

"AI makes mistakes. Build in a way to catch them." Three deliverables, all yours, all demoable:

**1. Build-time fit explanations.** Pre-generate `fitExplanation` for each resource × need pairing
in a one-off script (**never in the request path**). Commit the output. A human reads every string
before it ships. `fitExplanation` is model-authored prose *about* a record and is the one place a
fabricated claim can still appear.

**2. The link and claim checker.** A build-time script that:
- Fetches every `website` and `source_url`; flags non-200s, redirects to unrelated domains, and
  dead entries. The event repo found a listed shelter domain serving **gambling content** and
  another redirecting to a jobs site. Some hosts block plain fetches (caloptima.org 429,
  score.org 403, kiva.org 406, anaheim.net 403) — report these as "could not check", not as dead.
- Rejects generated copy matching a banned-phrase list: *"open now", "beds available",
  "you qualify", "guaranteed", "accepts your"*.
- Flags any record whose `verified_date` is stale or whose `confidence` is `medium` / `low`
  (6 and 2 of 40 respectively) so the client can label it.

**3. The 12-scenario eval harness.** Run
`challenges/2-finding-the-right-resource/scenarios/client-scenarios.json` end-to-end and print
pass/fail per scenario. The four that must pass:

| ID | Must do |
|---|---|
| **C02** | Spanish in, Spanish out; lands on `microenterprise` |
| **C10** | `route: danger`; hotline + staff; no address emitted |
| **C11** | `route: clinical`; refuses to advise; routes to a clinician |
| **C12** | Surfaces `verified_date` and `confidence` honestly |

This harness runs on stage. Make its output readable.

## WOW-FACTOR ITEMS YOU OWN

**Gate: only after the four needs work end-to-end and C02/C10/C11/C12 pass.** See the backlog in
`BRIDGE_SHARED_CONTEXT.md` for full rationale.

- **Scenario endpoint (~10 min of your side).** Serve `client-scenarios.json` so the client can
  offer a judge any of the 12, or free text. Highest score-per-minute item we have.
- **The gap report (~45 min).** Run 12 scenarios x 40 records; emit what Orange County does *not*
  have — needs with no match, needs where every option is referral-gated, records we could not
  verify. Output a page and a committed markdown file. 211OC has no taxonomy term for microloans or
  family reunification, so this is real information for IHH, not a consolation prize.
- **`staffSummary` (~30 min of your side).** A short plain summary of what the client asked for and
  what they already tried, for the client to hand to a caseworker if they choose. **Drafted, never
  sent.** Include nothing the client did not enter.
- **Plain-language `eligibility` (~20 min of your side).** Build-time rewrite of each record's
  eligibility text into plain words, human-reviewed, with the verbatim original kept alongside.
  Rewording supplied text only — invent nothing.
- **"Check this link now" (~30 min).** On-demand re-fetch of a `source_url` returning
  *unchanged* / *changed* / *could not check*. caloptima.org (429), score.org (403), kiva.org (406)
  and anaheim.net (403) refuse plain fetches — report those as *could not check*, never as dead.

## OFFLINE SUPPORT

- `GET /api/bundle` returns **one** cacheable payload: records, pre-generated explanations,
  next-step templates, and disclaimer copy (EN + ES). Everything except `/api/intake` must be
  answerable from it with no further network calls.
- `/api/intake` stays network-only with a deterministic fallback.
- `GET /api/health` lets the frontend tell "server down" from "no results".

## PRIVACY AND SECURITY

- **No identifiable patient or client information, ever.** Use only the 12 invented scenarios.
  Never commit, log, or send anything about a real person. This is a disqualification.
- Nothing connects to an IHH system. Nothing sends a message to anyone — draft only.
- No conversation persistence by default; no telemetry of raw user messages; redact sensitive input.
- `ANTHROPIC_API_KEY` from the server environment only, never in the web bundle or git history.
- Bound request size, configure CORS for the approved client origin, keep secrets out of error
  messages.
- Do not represent this prototype as HIPAA-compliant.

## FILES TO OWN (ILLUSTRATIVE)

```
supabase/migrations/*.sql                 schema + RLS (applied; committed)
supabase/functions/api/index.ts           the backend: /bundle, /intake, /health
supabase/seed.sql                         40 records, generated + round-trip verified
tools/generate-seed-sql.py                regenerates seed.sql from the event JSON
tools/check-links.py                      build-time link + claim checker        (TODO)
tools/run-scenarios.py                    12-scenario eval harness               (TODO)
```

DTOs still live client-side in `composeApp/.../api/ApiModels.kt` and mirror the seed schema
field-for-field. The database columns use the same names, so there is no mapping layer.

## ACCEPTANCE CRITERIA

- [x] Schema + RLS applied; anon can read resources, write nothing anywhere.
- [x] All 40 seed records imported and verified against the source file.
- [x] `GET /api/bundle` returns the shape the client already parses.
- [ ] `POST /api/intake` returns a classification (blocked on a workspace-scoped Anthropic key).
- [ ] Every shown resource detail traces to a seed record, with `source_url` and `source_quote`.
- [ ] `referral_required` resources return the referral route, never a walk-in address.
- [ ] Constraint filtering passes C06 (no phone use) and C08 (no phone at all).
- [ ] Safety router passes C10 (danger, address withheld) and C11 (clinical, refuses).
- [ ] C02 works end-to-end in Spanish and lands on `microenterprise`.
- [ ] No-match and model failure produce human-readable fallbacks, never a 5xx.
- [ ] `GET /api/bundle` is sufficient to run the client with no further network calls.
- [ ] Pre-generated explanations are committed, human-reviewed, and pass the banned-phrase check.
- [ ] Link checker runs and reports dead / unverifiable / stale entries.
- [ ] 12-scenario harness runs and prints pass/fail.
- [ ] Model ID is `claude-haiku-4-5` and a live request succeeds (verify before hour 2).
- [ ] No API key in the repo or its history.

## AGENT WORKING STYLE

Ask at most two **blocking** questions before starting (repo structure / contract). Make reasonable
reversible assumptions and document them. Implement the smallest working vertical slice. Never make
unrelated changes, never touch frontend-owned files without approval, never silently modify the
contract. Prefer clear named functions and short modular files over clever abstractions. Run
builds and tests, and report exact results.

**When the data is unclear or two sources disagree, show it — do not pick silently.** Say "unknown"
when it is unknown; a confident wrong answer is the worst possible outcome here.
