# Bridge web frontend (`:composeApp`)

Compose Multiplatform for Kotlin/Wasm. It's web-first and works offline after the first visit.

## Run it

Needs JDK 17–21 (Kotlin tooling is not reliable on JDK 26 yet).

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # or your JDK 21 path
./gradlew :composeApp:wasmJsBrowserDevelopmentRun                       # dev server with hot reload
./gradlew :composeApp:wasmJsBrowserDistribution                         # static build in composeApp/build/dist/wasmJs/productionExecutable
```

Add `-Pbridge.systemNode=true` to use the Node already installed on your machine. Gradle otherwise
downloads Node from nodejs.org, which can take 10+ minutes on slow Wi-Fi.

### Which backend?

- No query param: the app uses **built-in mock data**, shown under a red "DEMO DATA" banner. The records are fake placeholders.
- `?api=http://localhost:8081`: the app uses the real backend at that URL. The backend must allow CORS from the page's origin.

## How it talks to the backend

| Call | When | If it fails |
|---|---|---|
| `GET /api/bundle` | Once at startup. The result is saved in `localStorage`. | Uses the saved copy and shows "Working offline". If no copy exists, offers Try again or a 2-1-1 call. |
| `POST /api/intake` | Only when the user types free text | After 3s, silently asks the user to tap a goal tile. No error screen. |

Matching, fit explanations and next steps all come from the bundle, on the device. Free-text intake is the
only step that needs the network. The bundle JSON shape is **proposed** in
[`docs/bundle.sample.json`](../docs/bundle.sample.json) and mirrored in
`src/commonMain/kotlin/org/bridge/app/api/ApiModels.kt`. Swap that file for the backend's `:shared` DTOs once they exist.

Bundle fields the client relies on:

- `cities`: the cities the catalog covers. A city outside the list (for example "Tokyo") is an explicit no-match.
- `resources[].categories`: goal codes (`shelter`, `food`, `health`, `work`, `other`).
- `resources[].serviceArea`: city names, or `"*"` for the whole region.
- `explanations[resourceId][goal]`: the reviewed fit text, keyed by language.
- `nextSteps[resourceId]` with `nextSteps[goal]` as a fallback. If neither exists, generic copy is used.
- Localized text is `{"en": "...", "es": "..."}`. If Spanish is missing, English is shown.

## Offline

- `sw.js` caches the app shell network-first, so a new deploy shows up as soon as you're online. The shell
  is fully cached after the **second** online load, because the first load happens before the worker is in control.
  **Before a demo, open the page twice while online.**
- The bundle lives in `localStorage`, so it survives even without the service worker.

## Layout

```
src/commonMain/kotlin/org/bridge/app/
  App.kt                 screen routing
  api/                   DTOs, HTTP client, mock, contact-link safety (tel:/https only)
  domain/                offline matcher, bundle cache
  i18n/Strings.kt        all EN/ES copy
  ui/state/AppState.kt   screen stack and answers (in memory only)
  ui/screens/            one file per screen
  ui/components/         buttons with focus rings, scaffold (crisis bar, offline note), resource card
  ui/PrintCard.kt        HTML for the printable one-page card
src/wasmJsMain/          entry point, browser APIs, index.html with print CSS, sw.js
```
