BRIDGE — SHARED PRODUCT & ENGINEERING CONTEXT
Hackathon: Claude Community / Orange County Impact Lab
Duration: 5 hours | Team: 2 developers | Approach: Kotlin-first, web-first, offline-capable, mobile-ready

MISSION
Bridge helps people experiencing homelessness identify appropriate community resources and take one manageable next step, while preserving independence and offering optional caseworker assistance. It is NOT a substitute for caseworkers, crisis responders, clinicians, or verified service providers.

AUDIENCE AND ASSUMPTIONS
- Primary: people experiencing homelessness or housing instability, including people transitioning from recuperative care.
- Secondary: IHH frontline staff/caseworkers who can introduce Bridge and help when requested.
- Assumption for hackathon: user has intermittent or adequate internet and a device; do NOT pretend every client has these.
- Default: no account, no required phone number, no medical-document upload, no unnecessary sensitive data.
- Users choose goals and control what is shared. Use non-judgmental, non-patronizing, trauma-informed language.

SCOPE: ONE DEMO USER JOURNEY
1. Choose language (English/Spanish) and a goal (sleep/shelter, food, health, work, other).
2. Optional plain-language question, ONE at a time, about practical constraints (city, transportation, urgency); allow skip.
3. Retrieve 1–3 real, locally curated resources from verified records.
4. Show why each might fit, and explicitly disclose unverified eligibility/openings/hours.
5. Provide ONE immediately actionable next step: phone, website, questions to ask, checklist, and/or printable summary.
6. If no match or attempt failed, say so plainly and show an alternative or request caseworker assistance.

OFFLINE-FIRST / GRACEFUL DEGRADATION (CORE ARCHITECTURAL COMMITMENT)
Our users have intermittent data. The AI must therefore be a convenience layer, never a dependency.
- The critical path is deterministic: goal category -> curated resource records -> next step. This path MUST complete with zero network access.
- Claude is used ONLY for free-text intake (interpreting a typed sentence into need + constraints). When Claude is unreachable, slow, or returns invalid output, the app silently falls back to tapping a goal category. No dead ends, no spinners, no error pages.
- Resource records, fit explanations, and next-step copy are cached on the client. See "Cached payload" below.
- Fit explanations are PRE-GENERATED AT BUILD TIME by Claude and human-reviewed before shipping. This is both an offline enabler and a safety upgrade: every user-facing sentence about a resource has been read by a person.
- Cache boundary (decide in the first 20 minutes, do not bolt this on later):
  * Cached / offline-capable: app shell, UI copy (EN + ES), resources.json, pre-generated fit explanations, next-step templates, printable card.
  * Network-only with fallback: POST /api/intake (free-text understanding).
- Detect connectivity by fetch failure with a short timeout (~3s), NOT navigator.onLine, which reports false positives. A hung spinner is worse than no AI.

SAFETY & TRUST
- Never claim a resource has shelter beds, accepts a referral, guarantees eligibility, or is open NOW unless confirmed by an authorized real-time source.
- AI may interpret needs and phrase questions/explanations; it must NOT fabricate contacts, services, locations, availability, or medical guidance.
- Match ONLY against approved resource records. Show source URL and last-verified date; label status unknown if stale.
- Do not perform medical triage or make clinical eligibility/placement decisions. For emergencies, give locally appropriate emergency instructions and human support routes, without pretending Bridge can dispatch assistance.
- If official IHH/partner data is provided, inspect usage restrictions first. Use synthetic/non-identifying examples in the demo. Do not send PHI or identifiable client information to Claude without explicit permission and an appropriately approved data-handling arrangement.
- Don't store raw conversations by default. Minimize logs; redact sensitive input. API keys are server-only.
- Do not gamify homelessness, guilt users, or imply that inability to take action is a personal failure.

ACCESSIBILITY / DESIGN
- Mobile-first, readable large text (>=16px equivalent), strong contrast, generous spacing, large touch targets (~44px minimum), clear labels, and screen-reader semantics.
- Plain language; short paragraphs; one question/action per view; obvious Back, Skip, Start over controls.
- Text-first; voice OPTIONAL only if time permits; never rely on audio, map, GPS, or images as the sole access path.
- English and Spanish UI/copy; allow language switching without losing progress. Don't assume literacy, tech familiarity, or unrestricted mobile data.
- Respect user-selected need (don't overrule employment with shelter unless immediate safety needs are raised); offer options and explain choices.
- Confirm before external navigation/phone call; never auto-contact providers or staff.

TECHNICAL APPROACH
- Kotlin Multiplatform for shared DTOs, validation, and optionally reusable Compose Multiplatform UI.
- Kotlin/Ktor server hosts the Claude integration and curated resource data.
- Web demo FIRST. Android and iOS are follow-on targets, not mandatory for this 5-hour MVP.
- Kotlin/Wasm web support can carry toolchain/browser constraints: establish a running web build immediately; if blocked, agree on a simpler Kotlin-compatible web fallback rather than burn the hackathon.
- Keep client/server boundaries unambiguous. Keep shared business DTOs independent of platform APIs.

AGREED MVP API CONTRACT (adjust together once, in first 20 minutes)
POST /api/intake
  request: {"message":"...","language":"en","context":{"city":"Anaheim","goal":"shelter"}}
  response: {"need":"shelter","city":"Anaheim","constraints":[],"followUpQuestion":null,"needsHumanHelp":false}
POST /api/match
  request: {"need":"shelter","city":"Anaheim","constraints":[],"language":"en"}
  response: {"resources":[{"id":"res-1","name":"Example resource","description":"...","phone":null,"website":null,"address":null,"eligibilityNotes":"Must confirm","availability":"unknown","sourceUrl":"https://example.org","lastVerified":"2026-10-17","fitExplanation":"..."}],"disclaimer":"Confirm eligibility and availability directly."}
POST /api/plan
  request: {"resourceId":"res-1","language":"en","constraints":[]}
  response: {"title":"Your next step","steps":["Contact the provider to confirm eligibility"],"questionsToAsk":["Are services available?"],"alternatives":[],"staffSummary":null}
Note: Sample resource is a SCHEMA PLACEHOLDER, NOT A REAL SERVICE. Never show fictional resources in actual end-user demo.

TEAM AGREEMENTS
- Engineer 1 owns server, Claude prompt, data, matching, DTO definitions; Engineer 2 owns client UI/UX, app navigation, rendering, and accessibility.
- Decide DTO files and API contract in first 20 minutes; Engineer 1 publishes mocks immediately. Engineer 2 can use hard-coded fake JSON in development but must clearly label it and replace it for final demo.
- Avoid refactoring each other's directories without coordination. Commit working vertical slices. Keep main branch runnable.
- If time runs short, prioritize complete English web journey, verified links, and failure handling over accounts, maps, native builds, dashboards, or extra AI features.

DEMO CHOREOGRAPHY (THE WOW FACTOR - BUILD TOWARD THESE FIVE BEATS)
Judges at an impact lab are skeptical of AI slop. Our edge is provable restraint, not more AI features.
Open with the live demo in the first 20 seconds. No title slide, no architecture diagram. Phone-width viewport.

Act 1 - ONLINE (shows the AI earning its keep)
  Type a messy, realistic sentence: "got kicked out, no car, need somewhere tonight in Anaheim."
  Claude extracts need + city + constraint; 1-3 verified resources appear.

Act 2 - OFFLINE (the strongest 15 seconds available to us)
  Turn the laptop wifi OFF on stage. Reload. Tap the goal category instead of typing.
  Same verified resources, same source dates, same printable card.
  Say out loud: "The AI makes this faster. It's never what makes it work. Our users don't have
  reliable data, so neither does our critical path."
  NOTE: we are NOT claiming Claude runs offline - that is impossible and must never be implied.
  We are proving the app does not need Claude to be useful. Never fake connectivity (no phone
  hotspot theater); if a judge asks what is cached, answer plainly.

Beat 3 - DEMO OUR OWN FAILURE MODES
  Type something unanswerable ("I need a hotel in Tokyo tonight"). Show the explicit
  "no verified match" state instead of an invented provider. Deliberately showing what the app
  refuses to do reads as engineering maturity.

Beat 4 - "WHY AM I SEEING THIS?"
  Tap a resource card to reveal source URL, last-verified date, and which record fields drove the
  match. Turns our grounding rules into something visible in three seconds.

Beat 5 - ACCESS, DEMONSTRATED NOT CLAIMED
  Toggle English/Spanish mid-flow: one tap, copy flips, progress preserved.
  Then tab through the journey keyboard-only (or flip on a screen reader for ~15 seconds).
  Close on the printable one-page card: resource, phone number, questions to ask. Judges remember
  artifacts, and a library printout is a real access path for our users.

Pitch framing
- One line, not a story: "Bridge gets someone from a need to a verified phone number and a first
  question to ask, in under two minutes, offline, in two languages."
- Name what we deliberately refused to build: no accounts, no infinite chat, no streaks, no GPS,
  no gamification. Restraint is the pitch.
- Record a backup demo video before presenting. Event wifi dies.

RISK: PROTECT THE DEMO BEATS
Four of the five beats are client-side work. If the Kotlin/Wasm toolchain consumes hours 1-3, we
lose all of them. Therefore: if the web build is not rendering a styled component by T+60 minutes,
we switch to the agreed simpler fallback (Ktor-served static HTML + vanilla JS) without further
debate. Both engineers agree this tripwire in the first 20 minutes.

DEMO SUCCESS TESTS
- Client can move from need to a verified resource and a first action in under 2 minutes.
- No hallucinated provider or unconfirmed availability.
- Works at narrow phone width and keyboard navigation.
- Useful "no match" and "resource couldn't help" states.
- Human support remains optional and accessible.
- Full journey completes with wifi disabled, using the goal-category path.
- Claude timeout/failure degrades to the category path with no visible error state.
- "Why am I seeing this?" shows source URL and last-verified date for every resource.
- Language can be toggled mid-flow without losing progress.
- Journey is completable keyboard-only.
