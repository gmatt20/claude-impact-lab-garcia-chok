ROLE 1 — KOTLIN SHARED LOGIC & BACKEND ENGINEER
Project: Bridge | Read BRIDGE_SHARED_CONTEXT.md before coding.

YOUR MISSION
Own the reliable server-side intelligence that interprets user-described needs, matches only verified local community resources, and returns simple, actionable next steps. Support a web-first demo with Kotlin code portable where sensible to Android/iOS clients.

OWNERSHIP
- Kotlin/Ktor JVM backend: routes, validation, error handling, service layer, configuration.
- Shared Kotlin Multiplatform DTOs, enums, serialization, API client contract; use commonMain where applicable.
- Claude Messages API integration (server ONLY), with model configurable via CLAUDE_MODEL; default claude-haiku-4-5.
- Deterministic curated resource catalog and eligibility/matching filters.
- API responses for intake, matches, action plan, and user-facing non-match states.
- Build-time generation of pre-reviewed fit explanations (see OFFLINE SUPPORT below).
- The single cacheable offline payload the client ships with.
- Minimal verification/tests of data grounding and API results.

DO FIRST (FIRST 20 MINUTES)
1. Read shared context and inspect current repository/build setup; do not overwrite existing work.
2. Agree API paths, JSON shapes, and DTO location with Frontend Engineer.
3. Create the shared data classes and immediate mock endpoint responses. Announce endpoint contract to frontend partner.
4. Build/test one runnable request before adding Claude.

MVP API CONTRACT
- POST /api/intake: input {message,language,context}; return {need,city,constraints,followUpQuestion,needsHumanHelp}.
- POST /api/match: input {need,city,constraints,language}; return {resources,disclaimer}.
- POST /api/plan: input {resourceId,language,constraints}; return {title,steps,questionsToAsk,alternatives,staffSummary}.
- Consistent structured 4xx errors and predictable missing-data handling.
- Allow skipping intake: predetermined category selection should still reach /match without AI.

CLAUDE DESIGN
- Claude should classify need, extract a few practical constraints, and optionally compose a brief follow-up or concise explanation.
- Prefer the fast model claude-haiku-4-5 for intake. Consider claude-sonnet-5 ONLY if reliable extraction tests show a need and time/budget allow.
- Use the official Java SDK from Kotlin: implementation("com.anthropic:anthropic-java:2.34.0"), client via AnthropicOkHttpClient.fromEnv(). Do NOT hand-roll Ktor HTTP calls against the Messages API.
- Structured output is output_config: { format: {...} } (the older output_format parameter is deprecated). Use strict: true on tool definitions. Validate against Kotlin DTOs; clamp length and reject invalid values.
- claude-haiku-4-5 does NOT accept output_config.effort (it errors), and its thinking config uses the legacy budget_tokens form. For intake, use neither: plain fast structured output.
- Hard client-visible timeout of ~3s on the Claude call. On timeout, invalid JSON, or any API error, return the deterministic category path result rather than an error. The frontend must never show a spinner that outlives the timeout.
- Prompt instructions: empathetic, straightforward, never moralize, one question at a time, no assumptions about identity/health, invite skip.
- Resource names, locations, numbers, eligibility, and availability must ALWAYS originate from the curated dataset, not model memory.
- Do not give clinical advice, infer diagnoses, or make medical placement/referral decisions.
- Use model for language understanding, NOT as the source of truth. When it fails, fall back to straightforward category-based matching.

RESOURCE DATA
- Start with 8–15 genuinely verified, clearly sourced Orange County / Inland Empire support resources if authorized and available; prioritize IHH-provided/official data.
- Store record ID, name, category, service area/city, vetted contact info, eligibility caveats, source URL, lastCheckedAt, and availability=unknown unless verified.
- Never invent service details to fill a missing field. Null values are allowed.
- Match by goal, city/service area, and key constraints; deterministic filtering first, AI wording second.
- Return no-match explicitly; provide safe alternative to request human assistance or 211 where appropriate.

OFFLINE SUPPORT (DEMO-CRITICAL - SEE "DEMO CHOREOGRAPHY" IN SHARED CONTEXT)
The stage demo turns wifi off and completes the journey. That only works if you ship the data.
- Pre-generate fitExplanation strings for every resource x goal pairing at BUILD TIME via a one-off
  script (not in the request path). Commit the output. A human reads every string before it ships.
  This is simultaneously the offline enabler and our strongest safety control: no user-facing
  sentence about a resource is generated live and unreviewed.
- Expose GET /api/bundle returning ONE cacheable payload: resources, pre-generated explanations,
  next-step templates, and disclaimer copy (EN + ES). The client caches this; everything except
  /api/intake must be answerable from it.
- /api/intake stays network-only. Every other user-visible capability must be satisfiable offline.
- Guard the pre-generated copy: reject any string matching a banned-phrase list before commit -
  "open now", "beds available", "you qualify", "guaranteed", "accepts your". fitExplanation is
  model-authored prose ABOUT a record and is the one place a fabricated claim can still appear.
- Add GET /api/health so the frontend can distinguish "server down" from "no results".

PRIVACY AND SECURITY
- Treat health data as sensitive. Use synthetic examples unless IHH explicitly authorizes the supplied data's usage, storage, and API transmission.
- No default conversation persistence, no production telemetry of raw user messages, API key via server environment variable, never in web bundle.
- Bound request size, configure CORS for approved client origin, and prevent secrets in error messages.
- Do not represent this demo as HIPAA-compliant without review.

FILES TO OWN (ILLUSTRATIVE)
shared/src/commonMain/kotlin/.../models/*
server/src/main/kotlin/.../routes/*
server/src/main/kotlin/.../services/*
server/src/main/kotlin/.../ai/*
server/src/main/resources/resources.json
server/src/main/resources/explanations.json   (build-time generated, human-reviewed, committed)
tools/generate-explanations.kts               (one-off build-time script, NOT in request path)

ACCEPTANCE CRITERIA
[ ] Shared DTOs compile and frontend partner knows them.
[ ] Mock then live API flows are accessible to web client.
[ ] All shown resource details trace to records/source URLs.
[ ] No-match and model failure produce human-readable fallbacks.
[ ] English and Spanish are supported at least for intake/plan text.
[ ] Tests cover non-hallucination, missing location, empty dataset, invalid JSON and API failure.
[ ] GET /api/bundle returns everything the client needs to run with no further network calls.
[ ] Pre-generated fit explanations are committed, human-reviewed, and pass the banned-phrase check.
[ ] Claude timeout/failure returns a usable category-path result, never a 5xx to the client.
[ ] Model ID is claude-haiku-4-5 and a live request succeeds (verify before hour 2).

AGENT WORKING STYLE
Ask at most two BLOCKING questions before starting (repo structure / resource data access). Make reasonable reversible assumptions and document them. Implement the smallest working vertical slice. Never make unrelated changes, never touch frontend-owned files without approval, never silently modify the contract. Prefer clear named functions and short modular files over complex abstractions. Run builds/tests and report exact results. If external data can't be verified, surface the limitation and use clearly marked demo-only data rather than fabricating it.
