# Bridge

**Claude Community / Orange County Impact Lab** — 5-hour sprint, 2 developers.

Bridge helps people experiencing homelessness identify appropriate community resources and take
one manageable next step, while preserving independence and offering optional caseworker
assistance. It is **not** a substitute for caseworkers, crisis responders, clinicians, or verified
service providers.

> Bridge gets someone from a need to a verified phone number and a first question to ask — in
> under two minutes, offline, in two languages.

## The core commitment

The AI is a convenience layer, never a dependency. Our users have intermittent data, so the
critical path — goal category → curated resource records → next step — completes with **zero
network access**. Claude handles free-text intake only; when it is unreachable or slow, the app
degrades silently to tapping a goal category.

Every user-facing sentence about a resource is generated at build time and read by a human before
it ships. Resource names, numbers, eligibility, and availability come only from curated records,
never from model memory.

## Docs

| File | Read it for |
|---|---|
| [BRIDGE_SHARED_CONTEXT.md](BRIDGE_SHARED_CONTEXT.md) | Mission, safety rules, API contract, offline architecture, demo choreography. **Read first.** |
| [ROLE_1_BACKEND_SHARED_LOGIC.md](ROLE_1_BACKEND_SHARED_LOGIC.md) | Kotlin/Ktor server, shared DTOs, Claude integration, resource data |
| [ROLE_2_FRONTEND_CROSS_PLATFORM.md](ROLE_2_FRONTEND_CROSS_PLATFORM.md) | Compose Multiplatform web UI, accessibility, offline, demo beats |

## Stack

Kotlin Multiplatform shared DTOs · Kotlin/Ktor JVM server · Compose Multiplatform web (web first;
Android/iOS are follow-on targets) · Claude via the official Java SDK, server-side only.

## Configuration

Server-side only — the Claude API key must never reach the web bundle.

```
ANTHROPIC_API_KEY=sk-ant-...      # server environment only
CLAUDE_MODEL=claude-haiku-4-5     # fast model for intake classification
CORS_ALLOWED_ORIGIN=http://localhost:8080
```

## Status

Sprint scaffold in progress. Priority if time runs short: a complete English web journey with
verified links and good failure handling, ahead of accounts, maps, native builds, or extra AI
features.
