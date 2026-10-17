# Bridge

> For an IHH client leaving recuperative care: find the right next door in plain words, in your
> language, and know exactly what to do today.

**Team:** _[team name]_
**Members:** Matthew Garcia, Asher Chok
**Challenge:** 2 — Finding the right resource
**Built at:** Orange County | Claude Impact Lab, October 17, 2026, with Illumination Health + Home

## The problem

An IHH client getting ready to leave recuperative care has four practical questions: where do I
sleep, who can help me find work, can I get a small loan, and can anyone help me get home to
family? Orange County has programs for most of these, but finding the one that fits, working out
whether you qualify, and making the first call is where people get stuck. In IHH's words, clients
"can struggle searching for and accessing community resources to suit their specific needs."

## What we built

_[What it does. Screenshot or short clip here.]_

Bridge walks a client from a need to one concrete next step, using only the event's verified
resource records. Three things shape it:

- **It never sends anyone to a locked door.** Most shelters are referral-only, so the next step is
  the referral route, not an address and a call button.
- **Every claim shows its source.** Each card reveals the verbatim quote from the source page, the
  link, and the date it was checked.
- **It works with no signal.** The critical path is deterministic and cached; Claude handles
  free-text intake only, and the app degrades silently when it's unreachable.
- **One app, three places.** The same Kotlin Compose Multiplatform code runs as a website (for
  library and shared computers), an Android app and an iOS app. The mobile apps ship with every
  record built in, so the whole journey works with no data. Only provider websites need the
  internet; they open inside the app when online, and say "Internet connection required" when not.

**Demo video:** _[link]_

## How Claude is used

- **Building it:** Claude Code, against the event repo's seed data and scenarios.
- **At runtime:** Claude reads a client's typed sentence and extracts the need plus a few practical
  constraints. That is all. Resource names, phone numbers, addresses, hours, and eligibility always
  come from the seed records, never from the model.
- **What a person checks:** every user-facing sentence about a resource is generated at build time
  and read by a human before it ships. A build-time checker verifies source links and rejects
  unverifiable phrasing ("open now", "beds available", "you qualify").

## Try it

Requires JDK 21 and the bundled Gradle wrapper. All three apps share one module, `composeApp`.

```bash
git clone https://github.com/gmatt20/claude-impact-lab-garcia-chok.git
cd claude-impact-lab-garcia-chok
```

| Platform | Who it's for | How to run |
|---|---|---|
| **Web** | Library and shared computers; nothing to install | `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`, then open the printed localhost URL |
| **iOS** | Clients with an iPhone | Open `iosApp/iosApp.xcodeproj` in Xcode and run the `iosApp` scheme on a simulator or device. The first build compiles the Kotlin framework and takes a few minutes. |
| **Android** | Clients with an Android phone | Open the project in Android Studio and run `composeApp`, or `./gradlew :composeApp:installDebug` with a device or emulator attached |

For a static web build, run `./gradlew :composeApp:wasmJsBrowserDistribution`. The output is
`composeApp/build/dist/wasmJs/productionExecutable/`, which can be deployed to any static host. It
calls the same Supabase backend as the mobile apps.

Run the safety tests on every platform:

```bash
./gradlew :composeApp:wasmJsBrowserTest :composeApp:testDebugUnitTest :composeApp:iosSimulatorArm64Test
```

Tips:
- **Android:** needs the Android SDK (API 35). Android Studio writes `local.properties` for you.
- **iOS:** the Xcode project is generated from `iosApp/project.yml`. After editing that file, run
  `cd iosApp && xcodegen`.
- **Slow Wi-Fi:** add `-Pbridge.systemNode=true` to web tasks to use your installed Node.

Every build talks to the deployed Supabase backend by default. On the web you can override it per page:

| URL suffix | Effect |
|---|---|
| *(none)* | Uses the deployed Supabase Edge Function |
| `?api=off` | Forces the bundled-seed path (offline behaviour with wifi on) |
| `?api=https://...` | Points at a different backend |

**No API keys are needed to run any client.** The only secret in the system is
`ANTHROPIC_API_KEY`, which lives as a Supabase Edge Function secret and never reaches the browser or the apps.
See [supabase/README.md](supabase/README.md) for the backend.

## What is real and what is mocked

| Part | Status |
|---|---|
| Resource data (40 entries from the event seed file) | **real** — sourced, every entry checked 2026-10-13 |
| Database + row level security | **working** — anon reads resources, writes nothing anywhere |
| `GET /api/bundle` | **working** |
| Need matching, access routes, locked-door rule | **working** — client-side, runs offline |
| Safety routes (danger / clinical) | **working** — screens and deterministic keyword net |
| Spanish | **working** — all 94 UI strings; resource text stays in its source language |
| Offline mode | **working**. Web: service worker, cached bundle and seed fallback. Mobile: records built into the app plus the cached bundle. |
| Printable summary | **working**. Web print stylesheet; system print sheet on Android and iOS (also saves a PDF). |
| iOS app | **working**. Shared Compose UI; tested on the iPhone 17 Pro simulator. |
| Android app | **working**. Same UI and features as iOS; tested on an API 35 emulator, including airplane mode. |
| Provider websites in the app | **working** on mobile: in-app browser when online, "Internet connection required" when not. The web build opens a new tab. |
| `POST /api/intake` (Claude free-text) | **blocked** — needs a workspace-scoped Anthropic key |
| AI-written fit explanations | **not built** — table and RLS exist, no reviewed rows yet |
| Staff flagging of stale records | **not built** — table exists, no UI |

## Where it gets things wrong

- **Matching is single-category.** Seven of the twelve event scenarios name more than one need
  (C01 is `shelter` + `id-documents`); we match the first and ignore the rest. Record `tags` are
  not used in matching at all, so C01's walker and missing ID do not affect ranking.
- **We cannot tell you if a bed is free.** Nothing here is live availability, and we say so on
  every card rather than implying otherwise.
- **The data is a snapshot.** Every record was checked on 2026-10-13 and some will already be
  wrong — operators, hours and phone numbers change. We show the checked date and the source
  quote so a person can judge for themselves.
- **Free-text intake is the weakest link.** When Claude is unreachable the app silently falls back
  to tapping a category, which is the behaviour we want — but it means the typed-sentence path is
  the first thing to degrade.
- **Mobile testing so far is emulators and simulators only.** The offline web-page state was
  checked on Android in airplane mode. It hasn't been checked on iOS, because the simulator shares
  the Mac's network.
- **Provider websites are outside our control.** Inside the app they can only navigate to other
  web pages; links that try to call or open another app are blocked, so a site can't contact
  anyone on the client's behalf.
- **We would not trust it unsupervised** with anyone in crisis. The danger and clinical routes
  hand off to a person on purpose.

## Next steps

_[What it would take to pilot. What one more week would buy.]_

## Data and privacy

This project uses only data from the event repo and public sources. It contains no real patient or
client information. It does not connect to any Illumination Health + Home system, and it sends
nothing to anyone — outbound messages are drafted for a person to send, never sent.

Resource records come from `challenges/2-finding-the-right-resource/data/oc-resources.seed.json` in
the event repo. No directory was scraped; 211OC, OC Navigator, and findhelp.org were not accessed
programmatically.

## License

[MIT](LICENSE)

---

## Team docs

Planning docs for the sprint, not part of the submission narrative:

| File | Read it for |
|---|---|
| [BRIDGE_SHARED_CONTEXT.md](BRIDGE_SHARED_CONTEXT.md) | Challenge framing, judging criteria, rules, data schema, API contract, demo script |
| [ROLE_1_BACKEND_SHARED_LOGIC.md](ROLE_1_BACKEND_SHARED_LOGIC.md) | Ktor server, DTOs, matching, safety router, quality-control tooling |
| [ROLE_2_FRONTEND_CROSS_PLATFORM.md](ROLE_2_FRONTEND_CROSS_PLATFORM.md) | Compose web UI, resource card, accessibility, offline, demo beats |
| [composeApp/README.md](composeApp/README.md) | How the client is built: platforms, offline rules, safety rules in code, layout |
