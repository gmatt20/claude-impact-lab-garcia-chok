# BRIDGE — SHARED PRODUCT & ENGINEERING CONTEXT

**Event:** Claude Impact Lab, Orange County — October 17, 2026, with Illumination Health + Home (IHH)
**Challenge:** 2 — Finding the right resource
**Team:** 2 developers | **Approach:** Kotlin-first, web-first, offline-capable, mobile-ready

> Read this before coding. The event's own rules live in the challenge repo
> (`CLAUDE.md`, `docs/ground-rules.md`, `docs/judging.md`) and **override anything here.**

## THE CHALLENGE AS WRITTEN

> "How might we help someone getting back on their feet find the right next door, and walk through it?"

**Who it's for:** an IHH client getting ready to leave recuperative care, with four practical
questions. These four are the challenge — not a general Orange County directory:

| # | The client's question | Seed category |
|---|---|---|
| 1 | Where do I sleep? | `shelter` |
| 2 | Who can help me find work? | `employment` |
| 3 | Can I get a small loan? | `microenterprise` |
| 4 | Can anyone help me get home to family? | `family-reunification-travel` |

**Where the opportunity is.** The event repo says shelter and work "are well covered by public
sources," while "small loans and travel home to family are thin, and how IHH handles them today is
an open question." 211OC has **no taxonomy term at all** for microloans or for family
reunification. Most teams will build shelter search. Handling the two thin needs well is where we
differentiate — it is also where IHH gets something they do not already have.

**If we crack it:** "A person who leaves knowing exactly what to do today, in plain words, in their
language."

## WHO WE ARE BUILDING FOR (docs/background.md)

- IHH is a Santa Ana nonprofit running recuperative care, navigation centers, family emergency
  homes and permanent supportive housing. **2,287 of its 7,282 clients in 2024 were in recuperative
  care.**
- Recuperative care is short-term medical respite for people too ill to recover on the street, who
  don't need a hospital. Stays are **auto-authorized for 30 days, renewable in 30-day increments up
  to 90** — so every client has a known discharge horizon. Scenario C01 is five days out.
- **IHH clients manage their own daily care; staff cannot administer medications.** Staff monitor,
  teach and coordinate. A tool built for client independence matches how IHH already works — this
  is our day-one-impact argument, not a guess.
- Orange County counted **6,321 people experiencing homelessness in January 2026, 3,065
  unsheltered**, and among unsheltered adults **about a third reported a physical disability**.
  Accessibility is not polish here; it is a third of the population.

## MISSION

Bridge helps an IHH client identify appropriate community resources and take one manageable next
step, while preserving independence and offering optional staff assistance. It is **not** a
substitute for caseworkers, crisis responders, clinicians, or verified service providers.

## HOW WE WILL BE JUDGED (docs/judging.md)

| Weight | Area | The question judges answer |
|---|---|---|
| **40%** | Day-one impact | Is it ready to use, and would it help IHH on day one? |
| **35%** | Product and idea | Is it easy to use and to get started with? How creative and useful is the idea? |
| **25%** | Clarity of the demo | Are the problem, the user and the outcome obvious? |

The panel includes IHH staff, and **IHH's view of what would help on day one carries the most
weight.** Every build decision gets measured against "would an IHH caseworker use this Monday?"

**What does not place:** slides without a working demo; features that need IHH's live systems to
mean anything; anything built on real patient or client information (**disqualification**).

## THE DAY

| Time | What happens |
|---|---|
| 9:30 AM | Our work starts. Template files do not count; what we add after 9:30 does. |
| 4:00 PM | **Submission closes.** Judges review every repo and vote for the demos they want to see. |
| 4:30–5:30 PM | Finalist demos on the demo call. Keep the project running either way. |
| 5:45 PM | Winners announced. |

**Submission = a public GitHub repo (MIT) + a README following `templates/TEAM-README.md` + the
submission form.** A two-minute screen recording is optional but "makes judging much easier" —
and since judges pick finalists from the *submitted repo*, the README and video are a scoring
surface, not an afterthought. Treat 3:30 PM as the real deadline.

## RULES THAT DO NOT BEND (docs/ground-rules.md)

- **No identifiable patient or client information. From anyone. For any reason.** This is a
  disqualification. Use only the 12 invented scenarios in the event repo. Never type, paste, or
  commit anything about a real person — including anything either of us knows from our own work.
  If we see something that looks real, stop and tell an organizer.
- **Prototype only.** Nothing connects to, reads from, or writes to an IHH system. Nothing sends
  real messages. **Draft the message; do not send it.** Our tool sends nothing.
- **Build in quality control.** "AI makes mistakes. Build in a way to catch them." This is an
  explicit rule, and it is our single best scoring opportunity — see QUALITY CONTROL below.
- **Do not invent facts.** No made-up phone numbers, addresses, hours or eligibility rules. "When
  the source does not say, your tool says it does not know."
- **No medical, legal or immigration advice** anywhere client-facing. Point to staff.
- **Be straight in the demo.** "Show what works. Say what is mocked. Say where your tool gets
  things wrong. Judges will trust you more for it."
- **Open source, donated to IHH.** Public repo, MIT license, no secrets in git.

### Challenge-2 specific guardrails

- **Never invent a service fact.**
- **If someone says they are in danger: give the hotline, bring in staff.**
- **Never show the address of a domestic violence shelter.** (Scenario C10 tests this.)
- **Do not scrape 211OC, OC Navigator, or findhelp.org.** findhelp's terms explicitly forbid it.
  The documented route for 211 data is the national 211 API; for 211OC, ask Orange County United Way.
- **Never generate or "fix" a phone number, address or hours.** Unknown stays unknown.

## THE DATA: USE THE EVENT SEED FILE

`challenges/2-finding-the-right-resource/data/oc-resources.seed.json` — **40 entries**, every one
sourced from the organization's own page or a government page, with a verbatim `source_quote`
re-fetched and matched word-for-word on 2026-10-13.

**We do not build our own resource list.** This resolves our biggest risk. We may add entries from
`DATA-SOURCES.md` if time allows, but the seed is the baseline and it is already demo-ready.

Our DTOs mirror the seed schema exactly — do not rename fields:

```
id, name, organization, category, tags[], description, services[], eligibility,
population[], how_to_access, referral_required, phone, address, city, hours,
languages[], cost, website, source_url, source_quote, verified_date, confidence, notes
```

Three fields carry most of the product value:

- **`source_quote`** — the verbatim sentence from the source page. This is what makes "why am I
  seeing this?" credible rather than a claim. Show it.
- **`referral_required`** (7 of 40 true) and **`how_to_access`** (`mixed` 16, `phone` 7,
  `referral-only` 6, `online` 6, `walk-in` 4, `appointment` 1) — these prevent the failure the
  event repo warns about most bluntly: **"Most shelters are referral-only. A shelter address in
  your app can send someone to a locked door."**
- **`confidence`** (`high` 32, `medium` 6, `low` 2) and **`verified_date`** — surface both.

**20 of the 40 records have no `address` at all**, and 3 have no phone. An address therefore
*cannot* be the primary action for half the data — the locked-door design below is a data fact, not
a style preference. Note also that `wiseplace` (tags `women`, `survivors`) already ships with
`address: null`: the seed authors withheld it for us. Our own withholding rule stays as defense,
and becomes load-bearing the moment we add any record of our own from `DATA-SOURCES.md`.

### Categories (16, from the seed file)

`shelter`, `employment`, `microenterprise`, `family-reunification-travel`, `housing-navigation`,
`id-documents`, `transportation`, `phone-internet`, `benefits`, `food`, `health-coverage`,
`behavioral-health`, `legal`, `veterans`, `hygiene-mail-storage`, `other`

These are the wire values. The four headline needs lead the UI; the rest are reachable.

### What the seed file taught us (traps to design against)

Every one observed on 2026-10-13 and documented in `DATA-SOURCES.md`:

- Operators change; 211OC's record for Bridges at Kraemer Place still named the previous operator.
- **An address is not always the service site** — several records showed a head office.
- **Websites die or get taken over** — one listed shelter domain served gambling content; another
  redirected to an unrelated jobs site. **Check links before showing them.**
- Old listings linger; hours drift; phone numbers conflict; official material has typos.
- Closed doors are still signposted.

## QUALITY CONTROL (OUR SCORING EDGE — GROUND RULE 3)

"AI makes mistakes. Build in a way to catch them. How is up to you." Our answer, in three parts:

1. **Human-reviewed, build-time copy.** Every user-facing sentence about a resource is generated
   at build time by Claude and read by a person before it ships. Nothing about a resource is
   generated live and unreviewed.
2. **A link and claim checker.** A build-time script fetches every `website` and `source_url`,
   flags dead or hijacked domains, and runs a banned-phrase check over generated copy
   ("open now", "beds available", "you qualify", "guaranteed"). Directly answers the dead-domain
   trap above.
3. **The 12 scenarios as a test suite.** See below. Runnable, and shown on stage.

## THE 12 SCENARIOS ARE OUR ACCEPTANCE TESTS

`challenges/2-finding-the-right-resource/scenarios/client-scenarios.json` — 12 invented clients
with `needs`, `constraints`, and `in_their_words`. The organizer built these as tests. Four are
traps and we must pass all four:

| ID | Title | What it actually tests |
|---|---|---|
| **C02** | A small loan to start working again | **Written in Spanish.** Spanish is not optional. Plus `microenterprise`, a thin need. |
| **C10** | Not safe to go back ("He'll find me") | Danger route: hotline + staff, **and never render a DV shelter address.** |
| **C11** | "Can I just stop taking them?" | **Medical advice.** `needs` is deliberately empty. Must refuse and route to a clinician. |
| **C12** | "A guy told me there's a place... Is that real?" | Stale listings. Must surface `verified_date` and `confidence` honestly. |

The other eight test constraint handling: **C06** can't hear on the phone and asks to go see
somebody (→ must not return a phone-only resource); **C08** has no phone at all; **C01** needs an
accessible site, has no ID, basic phone only; **C05** is the ID catch-22; **C07** transportation;
**C04** employment + record; **C09** a veteran who never used the VA; **C03** travel home.

## SCOPE: ONE DEMO USER JOURNEY

1. Choose language (English/Spanish) and one of the four needs (plus "something else").
2. Optional plain-language question, ONE at a time, about practical constraints (city,
   transportation, phone access, urgency); always skippable.
3. Retrieve 1–3 resources from the seed file by deterministic filtering.
4. Show why each might fit, with `source_quote`, `source_url`, `verified_date`, and `confidence`.
   State plainly what is not confirmed.
5. Give ONE actionable next step, shaped by `how_to_access`: the referral route when
   `referral_required` is true, a phone number when calling works, a walk-in address only when the
   record supports it. Plus questions to ask, a checklist, and a printable summary.
6. If no match or the attempt failed, say so plainly and offer an alternative or staff assistance.

## OFFLINE-FIRST / GRACEFUL DEGRADATION

Our users have intermittent data. The AI is a convenience layer, never a dependency.

- The critical path is deterministic: need → seed records → next step. It MUST complete with zero
  network access.
- Claude is used only for free-text intake (interpreting a typed sentence into need + constraints)
  and for build-time copy. When Claude is unreachable or slow, the app silently falls back to
  tapping a need. No dead ends, no spinners, no error pages.
- Cache boundary — decide in the first 20 minutes, do not bolt this on later:
  - **Cached / offline:** app shell, UI copy (EN + ES), seed records, pre-generated explanations,
    next-step templates, printable card.
  - **Network-only with fallback:** `POST /api/intake`.
- Detect connectivity by fetch failure with a short timeout (~3s), **not** `navigator.onLine`,
  which reports false positives. A hung spinner is worse than no AI.

## ACCESSIBILITY / DESIGN

- Mobile-first; text ≥16px equivalent, strong contrast, generous spacing, ~44px touch targets,
  clear labels, screen-reader semantics.
- Plain language; short paragraphs; one question or action per view; obvious Back, Skip, Start over.
- Avoid jargon on screen: no "referral intake", "eligibility determination", "AI confidence".
- Text-first. Voice is a stretch goal, never a dependency. Never rely on audio, map, GPS or images
  as the sole access path.
- English and Spanish throughout, switchable without losing progress. (The County's own pocket
  guides ship in English, Spanish, Vietnamese, Korean and Chinese — worth naming as a next step.)
- Respect the user-selected need; offer options rather than overruling.
- Confirm before external navigation or a phone call. Never auto-contact anyone.
- Do not gamify homelessness, guilt users, or imply that inability to act is a personal failure.

## TECHNICAL APPROACH

- Kotlin Multiplatform for shared DTOs, validation, and optionally Compose Multiplatform UI.
- Kotlin/Ktor server hosts the Claude integration and the resource data.
- Web demo FIRST. Android and iOS are follow-on targets, not required today.
- Kotlin/Wasm can carry toolchain and browser constraints. Establish a running web build
  immediately.
- Keep client/server boundaries unambiguous; keep shared DTOs free of platform APIs.

## AGREED MVP API CONTRACT (adjust together once, in the first 20 minutes)

```
POST /api/intake
  req:  {"message":"...","language":"es","context":{"city":"Santa Ana","need":"shelter"}}
  res:  {"need":"shelter","city":"Santa Ana","constraints":["no-phone"],
         "followUpQuestion":null,"route":"normal","needsHumanHelp":false}
        route: "normal" | "danger" | "clinical" | "unknown"

POST /api/match
  req:  {"need":"shelter","city":"Santa Ana","constraints":["no-phone"],"language":"es"}
  res:  {"resources":[{ ...seed fields verbatim..., "fitExplanation":"...",
                        "accessRoute":"referral-only","addressWithheld":false }],
         "disclaimer":"Confirm eligibility and availability directly."}

POST /api/plan
  req:  {"resourceId":"211-orange-county","language":"es","constraints":[]}
  res:  {"title":"Your next step","steps":[...],"questionsToAsk":[...],
         "alternatives":[],"staffSummary":null}

GET /api/bundle   -> the single cacheable offline payload (records + generated copy + EN/ES UI copy)
GET /api/health   -> lets the client tell "server down" from "no results"
```

- Errors use one envelope: `{"error":{"code":"...","message":"..."}}`. No secrets in messages.
- Skipping intake must still reach `/api/match` with no AI involved.
- `route` drives safety: `danger` → hotline + staff, address withheld; `clinical` → refuse and
  route to a clinician; `unknown` → explicit no-match.

### Still to pin down together (first 20 minutes)

- [ ] Exact `constraints` element shape — flat slugs (`"no-phone"`) or objects. Pick one.
- [ ] Which 4 needs get UI tiles vs. live behind "something else".
- [ ] Whether `/api/plan`'s `alternatives` and `staffSummary` ship today or get cut.

## TEAM AGREEMENTS

- **Engineer 1** owns server, Claude prompt, data, matching, DTOs. **Engineer 2** owns client
  UI/UX, navigation, rendering, accessibility, and the TEAM-README.
- Decide DTOs and the contract in the first 20 minutes; Engineer 1 publishes mocks immediately.
  Engineer 2 may use labeled fake JSON in development, replaced before the demo.
- Engineer 1 solely owns `shared/.../models/`. Engineer 2 never edits it. Otherwise directories are
  disjoint; coordinate before touching the other's.
- Commit working vertical slices, often, with clear messages. Keep `main` runnable.
- If time runs short, prioritize a complete journey for the four needs with verified links, honest
  unknowns, and good failure handling — ahead of accounts, maps, native builds, or extra AI.

## RISK: PROTECT THE DEMO

Most of our scoring surface is client-side. If the Kotlin/Wasm toolchain consumes hours 1–3, we
lose it. **Tripwire: if the web build is not rendering a styled component by T+60 minutes, we
switch to Ktor-served static HTML + vanilla JS without further debate.** Agree this at 9:30.

## WOW-FACTOR BACKLOG (ONLY AFTER THE CORE JOURNEY PASSES THE 12 SCENARIOS)

**Gate: do not start any of these until the four needs work end-to-end and C02/C10/C11/C12 pass.**
A polished core beats a half-built extra. Ordered by score-per-hour.

### 1. Scenario picker — let a judge choose, or type their own (~20 min)
**Owners:** Engineer 2 (UI) + Engineer 1 (load `client-scenarios.json`).
A dropdown of the 12 scenarios plus a free-text box, reachable from the welcome screen.
The judging doc says that for Challenge 1 "judges may hand you a referral packet your tool has not
seen and ask you to run it." Offering the equivalent *before* being asked is the strongest
confidence signal available, and it costs us a `<select>`. Highest score-per-minute on this list.

### 2. The gap report (~45 min)
**Owner:** Engineer 1.
Run all 12 scenarios across all 40 records and emit a one-page report of what Orange County does
**not** have: which needs returned nothing, which returned only referral-gated options, which
records we could not verify. The repo hands us the premise — 211OC has no taxonomy term for
microloans or family reunification, and "how IHH handles them today is an open question."
This turns our limitation into a deliverable IHH can act on, and nobody else will build it.
Ship it as a page in the app and a markdown file in the repo.

### 3. Client-controlled staff handoff (~45 min)
**Owners:** Engineer 1 (generate) + Engineer 2 (share UI).
Fills the `staffSummary` field: a short plain summary of what the client is looking for and what
they have already tried, which the **client chooses** to hand to an IHH caseworker.
**Drafted, never sent** — ground rule 2. Serves both users at once: the client keeps control, the
caseworker skips the re-interview. Never include anything the client did not enter.

### 4. Plain-language eligibility, original one tap away (~30 min)
**Owners:** Engineer 1 (build-time) + Engineer 2 (toggle).
Seed `eligibility` text is bureaucratic. Show it in plain words with the verbatim original beside
it. This is the challenge statement's own phrase — "in plain words" — and it is the safest possible
use of Claude: rewording supplied text at build time, human-reviewed, inventing nothing.

### 5. Live "check this link now" (~30 min, stage risk)
**Owner:** Engineer 1.
Re-fetch a record's `source_url` on demand and show *still says this* / *changed* / *could not
check*. The gambling-domain trap makes this meaningful. But caloptima.org (429), score.org (403),
kiva.org (406) and anaheim.net (403) refuse plain fetches — so pre-run it, show cached results, and
keep exactly one known-good example for the live demo.

### 6. A third language — only if a human can read the output
OC's own pocket guides ship in Spanish, Vietnamese, Korean and Chinese, and 211OC lists Korean and
Vietnamese. A visible differentiator for an OC panel and cheap if copy is externalized.
**Skip it unless someone present can actually check the translation.** Bad Vietnamese is worse than
English-only, and we cannot verify what we cannot read.

## DEMO CHOREOGRAPHY (25% OF THE SCORE IS CLARITY)

Open with the live demo in the first 20 seconds. No title slide, no architecture diagram.
Phone-width viewport. One framing line:

> "Bridge gets an IHH client leaving recuperative care from a need to a verified next step — in
> plain words, in their language, in under two minutes, even with no signal."

**Beat 0 — put a person on screen before any UI.** Read C01 aloud: *"Ray, 62. Leaves recuperative
care in five days. Walks with a walker. Lost his ID."* Five days, one next step. True to the
30/90-day authorization reality, and it frames the whole demo around a clock rather than a feature.

**Beat 1 — a real scenario, read aloud.** Use **C02** verbatim: *"Yo sé trabajar. Solo necesito
como dos mil dólares para el equipo y el permiso."* Spanish in, Spanish out, and it lands on
`microenterprise` — a need 211OC cannot even categorize. This single beat covers language,
the thin need, and the client's own voice.

**Beat 2 — the locked door.** Show a `referral_required` shelter. We do not show an address and a
call button; we show how to actually get referred. Quote the repo: *"Most shelters are
referral-only. A shelter address in your app can send someone to a locked door."* This is the
day-one-impact moment for IHH staff.

**Beat 3 — "why am I seeing this?"** Reveal the verbatim `source_quote`, `source_url`,
`verified_date`, and confidence. Then **C12** — the maybe-stale listing — and show it says so.

**Beat 4 — the traps, run live.** **C11** (medical) → refuses, routes to a clinician.
**C10** (danger) → hotline and staff, address withheld. Say out loud that we built these on
purpose. Ground rule 7 explicitly rewards this.

**Beat 5 — quality control, on screen.** Run the 12-scenario suite and the link checker live.
"AI makes mistakes; here is how we catch them." This is ground rule 3 answered with a terminal.

**Beat 6 — wifi off.** Turn wifi off. Reload. Complete the journey by tapping a need. Close on the
printable card. *"The AI makes this faster. It's never what makes it work — our clients don't have
reliable data, so neither does our critical path."*
**We never claim or imply Claude runs offline.** No hotspot theater. If a judge asks what is
cached, answer plainly.

**Then:** name what we refused to build — no accounts, no infinite chat, no streaks, no GPS, no
gamification — and say where it gets things wrong. Record the two-minute video before 3:30.

## DEMO SUCCESS TESTS

- [ ] All 12 scenarios run, with the four traps (C02, C10, C11, C12) handled correctly.
- [ ] Client goes from need to a verified resource and a first action in under 2 minutes.
- [ ] No fabricated provider, phone, address, hours, or availability. Unknown shows as unknown.
- [ ] `referral_required` resources never present a walk-in address as the primary action.
- [ ] DV shelter addresses are never rendered.
- [ ] No medical, legal, or immigration advice is ever produced.
- [ ] Full journey completes with wifi disabled.
- [ ] Claude timeout or failure degrades to the need-category path with no visible error.
- [ ] "Why am I seeing this?" shows `source_quote`, `source_url`, and `verified_date`.
- [ ] Language toggles mid-flow without losing progress; C02 works end-to-end in Spanish.
- [ ] Works at phone width; completable keyboard-only.
- [ ] Useful "no match" and "this didn't help" states. Staff help stays optional and reachable.
- [ ] Repo is public, MIT-licensed, no secrets, README follows `templates/TEAM-README.md`.
