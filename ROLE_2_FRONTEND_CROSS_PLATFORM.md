# ROLE 2 — KOTLIN FRONTEND & CROSS-PLATFORM ENGINEER

**Project:** Bridge | **Challenge 2** — Finding the right resource
**Read `BRIDGE_SHARED_CONTEXT.md` first.** The event repo's `CLAUDE.md` and `docs/ground-rules.md`
override both.

## YOUR MISSION

Build a calm, welcoming, accessible, mobile-first experience for an IHH client leaving recuperative
care. Make the web demo work end-to-end first, keeping UI logic reusable on Android and iOS through
Compose Multiplatform where feasible.

**You own 25% of the score outright** (clarity of the demo) and most of the 35% for product and
idea. What judges see is almost entirely your surface.

## OWNERSHIP

- Compose Multiplatform web UI, screens, navigation, state management.
- Reusable client/UI modules and the shared API client where possible.
- English/Spanish copy and language-switching UX.
- Onboarding, need selection, single-question intake, resource cards, next-action view, and
  recoverable error states.
- Keyboard and screen-reader support, loading feedback, low-bandwidth-conscious design.
- Offline capability: service worker, cached bundle, connectivity degradation.
- **The team README** (`templates/TEAM-README.md` shape) and the two-minute demo video.
- Final demo experience and cross-browser / phone-width smoke tests.

## DO FIRST (FIRST 20 MINUTES)

1. Read the shared context. Inspect the repo and toolchains, and run a web hello-world build
   **immediately** — this is the single biggest schedule risk today.
2. Align the API contract and DTOs with the Backend Engineer; get mock JSON.
3. Build a complete click-through demo on mocks while server work proceeds.
4. Integrate real endpoints once available. **Do not spend the day debugging native builds.**

**Tripwire:** if the web build is not rendering a styled component by T+60 minutes, switch to
Ktor-served static HTML + vanilla JS. Agreed in advance, no debate at the time.

## USER JOURNEY (MVP)

- **Welcome:** "Find your next step" / "Encuentra tu próximo paso", English/Spanish choice,
  brief disclaimer.
- **Need selection:** lead with IHH's four named needs, in the client's words, not ours:
  - *A safe place to sleep* → `shelter`
  - *Help finding work* → `employment`
  - *A small loan to get working* → `microenterprise`
  - *Getting home to family* → `family-reunification-travel`
  - *Something else* → the remaining 12 seed categories
- **Single follow-up if needed:** city, transportation, phone access, or urgency. Always skippable.
- **Results:** at most 3 records. Name, fit explanation, how to access, source, verification caveat.
- **Detail:** primary action shaped by `how_to_access`, "What should I ask?", one concrete next step.
- **If not suitable:** "This didn't work" / "Try another option" / "Ask a staff member for help".
  No guilt, no progress scoring.
- **Printable summary.** No account creation, ever.

## THE RESOURCE CARD IS THE PRODUCT

Everything the judges score lives on this card. Render these seed fields deliberately:

| Field | How it shows up |
|---|---|
| `referral_required` / `how_to_access` | **Drives the primary action.** See below — this is the most important rule in the doc. |
| `source_quote` | The verbatim sentence from the source page, behind "Why am I seeing this?" |
| `source_url` + `verified_date` | "Checked 13 Oct 2026" with a link out |
| `confidence` | `medium` / `low` gets a plain-language caveat, not a number or a score |
| `phone`, `address`, `city`, `hours` | Only when present. **20 of 40 records have no address and 3 have no phone** — null is the normal case, not the edge case. Never render an empty or broken button |
| `languages` | Show when the client picked Spanish and the record lists it |
| `cost`, `eligibility` | Plain text, uncut. Do not summarize away a requirement |

### The locked-door rule

The event repo's bluntest warning: **"Most shelters are referral-only. A shelter address in your app
can send someone to a locked door."**

- When `referral_required` is true (7 of 40 records), the primary action is **how to get referred** —
  never an address with a walk-in affordance.
- `how_to_access` decides the primary action: `phone` → call; `walk-in` → address; `online` → link;
  `referral-only` / `appointment` → the referral or booking route; `mixed` → show the options.
- `address` is display-only context. Several source records held a head office rather than the
  service site, so never present an address as "go here" unless `how_to_access` supports it.
  **Half the records have no address at all**, so a layout that leans on address will look broken
  on most cards.
- Build `tel:` and external links **only** from values actually present in the record.

### Safety rendering

- When the response carries `addressWithheld: true`, **never render the address**, even if the field
  is populated. Domestic violence shelter addresses are never shown. (Scenario C10.)
- When `route` is `clinical`, show the clinician/staff route and **no advice**. (Scenario C11.)
- Nothing client-facing gives medical, legal, or immigration advice.
- A persistent, static **"Need help now?"** affordance on every screen with 988 / 911 / 211.
  No AI in the path, no detection logic, works offline. Required for a public demo of this product.

## DEMO BEATS YOU OWN

Read "DEMO CHOREOGRAPHY" in the shared context. Four of the six beats are yours:

**1. Offline completion (build it early).** Service worker caches the app shell, EN+ES copy, and the
`GET /api/bundle` payload. With wifi off, Welcome → Need → Results → Next Step → printable card must
fully complete. Detect connectivity by fetch failure with a ~3s timeout, **not** `navigator.onLine`
(it reports false positives). On failure, silently route to need selection. Never show a spinner
that outlives the timeout, and never an error page for a missing AI call. A small calm "Working
offline" indicator is fine — **never imply the AI is local.**

**2. "Why am I seeing this?"** A disclosure on each card revealing `source_quote` verbatim,
`source_url`, `verified_date`, and the confidence caveat. Plain language — no "match score", no
"AI confidence". Keyboard reachable and announced to screen readers.

**3. Language toggle mid-flow.** One tap flips all copy EN ↔ ES with progress fully preserved, and
it must work offline. **Scenario C02 is written in Spanish** ("Yo sé trabajar. Solo necesito como dos
mil dólares...") and is our opening demo beat — Spanish is not optional.

**4. Access, demonstrated not claimed.** The whole journey completable keyboard-only with visible
focus rings, good enough to tab through live on stage. Printable one-page card as the closing
artifact: name, how to access, phone, questions to ask, source and checked date. Use a real print
stylesheet and test actual print preview.

**Also yours: the no-match state.** The demo deliberately shows an unanswerable request. Make it
calm, clearly worded, and offer the staff route. It gets stage time — treat it as a feature.

**Also yours: the stale-listing state.** Scenario C12 asks "Is that real?" about a possibly-dead
listing. Show `verified_date` and the confidence caveat honestly rather than hiding uncertainty.

## WOW-FACTOR ITEMS YOU OWN

**Gate: only after the four needs work end-to-end and C02/C10/C11/C12 pass.** A polished core beats
a half-built extra. Full rationale in `BRIDGE_SHARED_CONTEXT.md`.

- **Scenario picker (~20 min) — build this one.** A dropdown of the 12 event scenarios plus a
  free-text box, reachable from the welcome screen, so a judge can pick one or type their own. The
  judging doc says judges may hand Challenge 1 teams an unseen packet; offering the equivalent
  before being asked is the strongest confidence signal available to us, for the price of a
  `<select>`. Highest score-per-minute item on the whole list.
- **Plain-language eligibility toggle (~10 min of your side).** Show the plain-words version with
  the verbatim original one tap away. This is the challenge statement's own phrase: "in plain words".
- **Share-with-staff affordance (~15 min of your side).** Renders `staffSummary` for the client to
  hand over **if they choose**. Make "drafted, not sent" visible on screen — it is ground rule 2,
  and judges will notice we understood it.
- **The gap report page.** Engineer 1 generates it; give it a readable page. It tells IHH where
  Orange County has nothing, which is a takeaway they can act on Monday.

## UX NON-NEGOTIABLES

- Clarity over novelty. One task per screen; large readable text, high contrast, generous spacing,
  ~44px+ touch targets, visible focus rings, semantic labels.
- Plain language. No jargon on screen: no "referral intake", "eligibility determination",
  "AI confidence".
- Never require a diagnosis, legal status, exact sleeping location, SSN, or other private detail.
- Manual city selection; no GPS requirement. Always offer Back, Skip, Start Over. Never make
  infinite chat the only workflow.
- No shame-based copy, fake urgency, fake success celebrations, streaks, or gamification.
- Show "Availability not confirmed — contact the provider" where appropriate.
- Never ask the user to trust AI blindly. Every record shows its source.
- Text-only must work with no microphone or media downloads. Voice is a stretch goal, never a
  dependency.
- **Never auto-call, auto-email, or submit anything to an organization.** Our tool sends nothing;
  every outbound action requires explicit user action. (Ground rule 2.)

## UI MODULES (SUGGESTED)

```
shared/src/commonMain/kotlin/.../ui/screens/WelcomeScreen.kt
shared/src/commonMain/kotlin/.../ui/screens/NeedScreen.kt
shared/src/commonMain/kotlin/.../ui/screens/IntakeScreen.kt
shared/src/commonMain/kotlin/.../ui/screens/ResourcesScreen.kt
shared/src/commonMain/kotlin/.../ui/screens/NextStepScreen.kt
shared/src/commonMain/kotlin/.../ui/components/ResourceCard.kt
shared/src/commonMain/kotlin/.../ui/components/WhyThisDisclosure.kt
shared/src/commonMain/kotlin/.../ui/components/CrisisBanner.kt
shared/src/commonMain/kotlin/.../ui/state/*
webApp/...   (actual entrypoint per the generated project)
```

Do not assume these paths exist — follow the real Gradle wizard layout.

## INTEGRATION

- Consume the shared DTOs and endpoints in `BRIDGE_SHARED_CONTEXT.md`. DTO field names mirror the
  event seed file (`verified_date`, `source_quote`, `how_to_access`, `referral_required`,
  `confidence`) — use them as-is.
- Fetch `GET /api/bundle` once and cache it. Every screen except free-text intake must render from
  the cache with no network.
- Loading: "Finding possible options…". Error: "We couldn't load results. You can try again or ask
  for help."
- **A failed `/api/intake` is not an error state** — degrade silently to need selection.
- Guard every optional field so a missing website, phone, or address never produces an empty or
  broken button. Nulls are common in the real data.
- In early development use mock data **labeled as mock**; replace before the demo. Ground rule 7:
  "Show what works. Say what is mocked."

## THE SUBMISSION IS A SCORING SURFACE

Judges pick finalists from the **submitted repo** at 4:00 PM, before any live demo. So:

- [ ] README follows `templates/TEAM-README.md`: who it's for, the problem, what we built, how
      Claude is used, exact run steps, **what is real vs mocked**, **where it gets things wrong**,
      next steps, data and privacy, license.
- [ ] Repo public, MIT `LICENSE` present, no secrets in the repo or its history.
- [ ] Someone not on the team could follow the README and run it.
- [ ] Two-minute screen recording linked in the README. **Record it by 3:30 PM** — it doubles as the
      backup if event wifi dies during the live demo.

## ACCEPTANCE CRITERIA

- [ ] Main web path usable at phone viewport with large touch targets.
- [ ] Welcome → Need → Result → Next Step in under ~2 minutes.
- [ ] Every screen has understandable Back / Skip where relevant.
- [ ] English and Spanish present; C02 works end-to-end in Spanish.
- [ ] Language toggles mid-flow without losing progress, offline included.
- [ ] `referral_required` records show the referral route, never a walk-in address.
- [ ] `addressWithheld` is honored; DV addresses never render.
- [ ] Crisis route reachable from every screen, offline included.
- [ ] "Why am I seeing this?" shows `source_quote`, `source_url`, `verified_date`; keyboard reachable.
- [ ] Unknown availability and `medium`/`low` confidence are shown clearly; empty results handled.
- [ ] Full journey completes with wifi disabled (test by actually turning wifi off).
- [ ] `/api/intake` timeout or failure degrades with no visible error.
- [ ] Keyboard navigation and visible focus tested end-to-end.
- [ ] API errors are recoverable; no blank crash page.
- [ ] Printable card tested in real print preview.
- [ ] Client never contains the Claude API key.

## AGENT WORKING STYLE

Ask at most two **blocking** questions before coding (frontend scaffold / API contract). Make
reasonable reversible assumptions and document them. Prefer small reusable Compose components and
explicit UI state over giant files. Ship a functioning vertical slice first. Don't change server
code or the shared DTO schema without partner agreement. Run the web build and report exact status.
Keep Android/iOS portability in mind, but never sacrifice a complete web demo for untested platform
polish.

**Say "unknown" when it is unknown.** A confident wrong answer is the worst outcome here.
