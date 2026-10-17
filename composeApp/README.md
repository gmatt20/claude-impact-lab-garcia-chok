# Bridge web frontend (`:composeApp`)

Compose Multiplatform for Kotlin/Wasm. It's web-first and works offline after the first visit.

## Run it

Needs JDK 17–21 (Kotlin tooling is not reliable on JDK 26 yet) and Google Chrome for the tests.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # or your JDK 21 path
./gradlew :composeApp:wasmJsBrowserDevelopmentRun   # dev server with hot reload
./gradlew :composeApp:wasmJsBrowserDistribution     # static build in composeApp/build/dist/wasmJs/productionExecutable
./gradlew :composeApp:wasmJsBrowserTest             # safety-rule tests, headless Chrome
```

Add `-Pbridge.systemNode=true` to use the Node already installed on your machine. Gradle otherwise
downloads Node from nodejs.org, which can take 10+ minutes on slow Wi-Fi.

### Data source

- **No query param:** the app runs on the **event seed file** shipped with it
  (`src/wasmJsMain/resources/seed/oc-resources.seed.json`, 40 real, sourced records, MIT, from
  `travcjohnson/claude-impact-lab-oc`). There's no mock data, but free-text intake is hidden because it needs the server.
- **`?api=http://localhost:8081`:** the app uses the backend. It fetches `GET /api/bundle` and caches it on the
  device, and free text goes to `POST /api/intake`. The backend must allow CORS from the page's origin.

## How it talks to the backend

| Call | When | If it fails |
|---|---|---|
| `GET /api/bundle` | Once at startup. The result is saved in `localStorage`. | Uses the saved copy and shows "Working offline". If no copy exists, falls back to the shipped seed file. |
| `POST /api/intake` | Only when the user types free text | After 3s, silently asks the user to tap a need tile. No error screen. |

Matching runs on the device and is deterministic: category first, then city, then the phone constraint, then confidence.

**Bundle shape (proposed):** the seed file's shape plus optional reviewed explanations, so the raw seed
file is itself a valid bundle:

```json
{ "generated": "2026-10-13", "disclaimer": "...", "resources": [ /* seed records, verbatim fields */ ],
  "explanations": { "<resourceId>": { "<need>": { "en": "...", "es": "..." } } } }
```

Records may also carry the server's `addressWithheld` and `fitExplanation`. DTO mirror:
`src/commonMain/kotlin/org/bridge/app/api/ApiModels.kt`. Swap it for the backend's `shared` models once they exist.

**Intake `route`:**
- `danger` shows the hotline/staff screen and hides every address until Start over.
- `clinical` shows "ask your care team", with no advice.
- `unknown` shows an explicit no-match.

**Constraints** are flat slugs. The client sets `no-phone` and `phone-hard`.

## Safety rules enforced in the client

- **Locked door:** `referral_required` or `how_to_access: referral-only` means the next step is getting a
  referral. The address only becomes a destination for `walk-in`; otherwise it's labelled "may be an office" or
  "don't go here without a referral".
- **Address withholding:** a survivor-serving or confidential program's address is never rendered. That covers
  `addressWithheld`, `survivors`/DV tags, and DV populations.
- **Call buttons:** built only from numbers literally present in the `phone` text. The raw text is always shown too.
- **Honest uncertainty:** `medium`/`low` confidence gets a plain-language caveat. The source quote, link, checked
  date and checker notes sit behind "Why am I seeing this?". Availability always reads "not confirmed".

Tests: `src/commonTest/kotlin/org/bridge/app/SafetyRulesTest.kt`.

## Offline

- `sw.js` caches the app shell and seed file network-first. The shell is fully cached after the **second**
  online load. **Before a demo, open the page twice while online.**
- The backend bundle lives in `localStorage`, so it survives even without the service worker.

## Layout

```
src/commonMain/kotlin/org/bridge/app/
  App.kt                 screen routing
  api/                   DTOs (seed schema), HTTP client, phone/link safety
  domain/                access rules (locked door, DV withholding), offline matcher, bundle loading
  i18n/                  EN/ES copy, 16 need labels
  ui/state/AppState.kt   screen stack and answers (in memory only)
  ui/screens/            one file per screen
  ui/components/         resource card, crisis bar, focus-ring buttons
  ui/PrintCard.kt        HTML for the printable one-page card
src/wasmJsMain/          entry point, browser APIs, index.html with print CSS, sw.js, seed file
```
