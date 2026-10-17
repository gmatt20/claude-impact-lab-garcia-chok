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

```bash
# exact steps to run it on a laptop
```

Requires _[JDK / Gradle versions]_. Environment variables, server-side only:

```
ANTHROPIC_API_KEY=sk-ant-...      # never in the repo or the web bundle
CLAUDE_MODEL=claude-haiku-4-5
CORS_ALLOWED_ORIGIN=http://localhost:8080
```

## What is real and what is mocked

| Part | Status |
|---|---|
| Resource data (40 entries from the event seed file) | real, sourced, checked 2026-10-13 |
| Need matching and access-route logic | _[working / partly working / mocked]_ |
| Claude free-text intake | _[…]_ |
| Spanish copy | _[…]_ |
| Offline mode | _[…]_ |
| Printable summary | _[…]_ |

## Where it gets things wrong

_[What we saw it miss, and what we would not trust it with yet. Be specific — judges trust this.]_

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
