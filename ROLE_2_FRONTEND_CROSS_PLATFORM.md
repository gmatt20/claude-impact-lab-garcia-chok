ROLE 2 — KOTLIN FRONTEND & CROSS-PLATFORM ENGINEER
Project: Bridge | Read BRIDGE_SHARED_CONTEXT.md before coding.

YOUR MISSION
Build a calm, welcoming, accessible, mobile-first Bridge experience for people experiencing homelessness. Make the web demo work end-to-end first, while keeping UI logic/components reusable on Android and iOS through Compose Multiplatform where feasible.

OWNERSHIP
- Compose Multiplatform web UI, screens, navigation and state management.
- Reusable client/UI modules and shared API client where possible.
- English/Spanish copy and language-switching UX.
- Onboarding, needs category choice, single-question intake, resource cards, next-action view and recoverable error states.
- Keyboard/screen-reader support, loading feedback, and low-bandwidth-conscious design.
- Offline capability: service worker, cached bundle, and connectivity degradation.
- The four client-side demo beats (see DEMO BEATS YOU OWN below).
- Final demo experience and cross-browser/phone-width smoke tests.

DO FIRST (FIRST 20 MINUTES)
1. Read shared context, inspect repo/toolchains, and run a web hello-world build immediately.
2. Align API contract/DTO models with Backend Engineer, obtain mock JSON responses.
3. Implement a complete click-through demo using mock responses while server work proceeds.
4. Integrate real endpoints once available. Don't spend most of the hackathon debugging native builds.

USER JOURNEY (MVP)
- Welcome: "Find your next step" / "Encuentra tu próximo paso" with English/Spanish selection and optional disclaimer.
- Goal selection: safe place to sleep, food, health support, work, something else (no mandatory narrative).
- Single follow-up if necessary: city / transport / urgent constraint; make every question optional or skippable when safe.
- Resource results: show at most 3 suitable records; title, fit explanation, confirmed contact details, source, verification caveat.
- Resource detail: primary action (call/open website), "What should I ask?", and one concrete next step.
- If not suitable: "This didn't work" / "Try another option" / "Ask for help from a caseworker". Do not guilt or score progress.
- Save/print/share plan only if easy; avoid account creation.

DEMO BEATS YOU OWN (THE WOW FACTOR - FOUR OF THE FIVE ARE YOURS)
Read "DEMO CHOREOGRAPHY" in BRIDGE_SHARED_CONTEXT.md first. These are what the judges actually see.

1. OFFLINE COMPLETION (highest value, build it early)
   - Service worker caches: app shell, UI copy EN+ES, and the GET /api/bundle payload
     (resources, pre-generated explanations, next-step templates, disclaimers).
   - With wifi off, Welcome -> Goal -> Results -> Next Step -> printable card must fully complete.
   - Detect connectivity by fetch failure with a ~3s timeout, NOT navigator.onLine (it reports
     false positives). On failure, silently route to the goal-category path. Never show a spinner
     that outlives the timeout, and never show an error page for a missing AI call.
   - Optional honest touch: a small, calm "Working offline" indicator. Never imply the AI is local.

2. "WHY AM I SEEING THIS?"
   - A disclosure on each resource card revealing source URL, last-verified date, and which record
     fields drove the match. Plain language, not "match score" or "AI confidence".
   - Must be reachable by keyboard and announced to screen readers.

3. LANGUAGE TOGGLE MID-FLOW
   - One tap flips all copy EN <-> ES with progress fully preserved. Must work offline.
   - This is a two-second visual moment on stage - make the transition clean, not janky.

4. ACCESS, DEMONSTRATED NOT CLAIMED
   - The entire journey must be completable keyboard-only with visible focus rings, good enough to
     tab through live on stage.
   - Printable one-page card as the closing artifact: resource name, phone, address, questions to
     ask, source and last-verified date. Use a real print stylesheet - test actual print preview.

ALSO YOURS: THE EXPLICIT NO-MATCH STATE
   The demo deliberately types something unanswerable ("a hotel in Tokyo tonight") to show we do
   not invent providers. Make that state calm, clearly worded, and offer the caseworker-help route.
   This screen gets stage time - treat it as a feature, not an edge case.

ALSO YOURS: ALWAYS-REACHABLE CRISIS ROUTE
   A persistent, static "Need help now?" affordance with 988 / 911 / 211 copy. No AI in the path,
   no detection logic, works offline. Required for a public demo of this product.

UX NON-NEGOTIABLES
- Prefer clarity over novelty. One task per screen; large readable text, high contrast, generous spacing, touch targets approximately 44px+, visible focus rings, semantic labels.
- Plain-language short instructions; avoid jargon like "referral intake", "eligibility determination", and "AI confidence" in client UI.
- Never require users to disclose diagnosis, legal status, exact sleeping location, SSN, or other private details.
- Allow manual city selection; no GPS requirement. Provide Back, Skip, Start Over. Avoid infinite chat as the only workflow.
- No shame-based copy, fake urgency, fake success celebrations, streaks, or gamification of homelessness.
- Explicitly show "Availability not confirmed; contact provider" when appropriate.
- Do not ask users to trust AI blindly. Resource entries should show trusted sources.
- Text-only experience must work even without microphone or media downloads. Voice can be a stretch goal, not a dependency.
- Don't auto-call, auto-email, or submit data to an organization. Require explicit user action.

UI MODULES (SUGGESTED)
shared/src/commonMain/kotlin/.../ui/screens/WelcomeScreen.kt
shared/src/commonMain/kotlin/.../ui/screens/GoalScreen.kt
shared/src/commonMain/kotlin/.../ui/screens/IntakeScreen.kt
shared/src/commonMain/kotlin/.../ui/screens/ResourcesScreen.kt
shared/src/commonMain/kotlin/.../ui/screens/NextStepScreen.kt
shared/src/commonMain/kotlin/.../ui/components/*
shared/src/commonMain/kotlin/.../ui/state/*
webApp/... (actual entrypoint based on generated project)
Do not assume these paths already exist: follow the real Gradle wizard layout.

INTEGRATION
- Consume shared DTOs and backend endpoints defined in BRIDGE_SHARED_CONTEXT.md.
- In early development, use mock data labeled as mock. Before pitching, replace with backend verified records.
- Consume GET /api/bundle once and cache it; every screen except free-text intake must render from the cache with no network.
- Loading: "Finding possible options..."; error: "We couldn't load results. You can try again or ask for assistance."
- A failed /api/intake is NOT an error state - degrade to goal-category selection silently.
- Guard optional fields so missing website/phone/address doesn't create empty or broken buttons.
- Build safe external links and tel: actions only for valid contact values; never invent contacts.

ACCEPTANCE CRITERIA
[ ] Main web path usable at a phone viewport with large touch targets.
[ ] From Welcome -> Goal -> Result -> Next Step in under ~2 minutes.
[ ] Every screen has understandable Back / Skip where relevant.
[ ] English/Spanish copy present and language can be switched.
[ ] Shows unknown availability clearly and handles empty results.
[ ] Keyboard navigation and visible focus tested.
[ ] API errors are recoverable; no blank crash page.
[ ] Client never contains Claude API key.
[ ] Full journey completes with wifi disabled (test by actually turning wifi off).
[ ] /api/intake timeout or failure degrades to the category path with no visible error.
[ ] "Why am I seeing this?" shows source URL and last-verified date, keyboard reachable.
[ ] Language toggles mid-flow without losing progress, offline included.
[ ] Printable card tested in real print preview.
[ ] Crisis route reachable from every screen, offline included.

AGENT WORKING STYLE
Ask at most two BLOCKING questions before coding (frontend scaffold / API contract). Make reasonable reversible assumptions and document them. Prefer reusable small Compose components and explicit UI state over giant files. Implement a functioning vertical slice first. Don't change server implementation or shared DTO schema without partner agreement. Run the web build and report exact status. Keep Android/iOS portability in mind but do not sacrifice a complete web demo for untested platform polish.
