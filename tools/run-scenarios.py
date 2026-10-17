#!/usr/bin/env python3
"""Run the event's 12 client scenarios against the live intake endpoint.

Quality control (ground rule 3): AI makes mistakes, so check them. Usage:
    python3 tools/run-scenarios.py [path-to-client-scenarios.json]
"""
import json, sys, time, urllib.request

API = "https://ntgqdwfpkbmzodgygbxi.supabase.co/functions/v1/api/intake"

# What each scenario must do. None = no hard assertion, just report.
EXPECT = {
    "C02": ("microenterprise", "normal"),
    "C10": (None, "danger"),
    "C11": (None, "clinical"),
    "C12": (None, None),
}

def call(message, language):
    body = json.dumps({"message": message, "language": language, "context": {}}).encode()
    req = urllib.request.Request(API, data=body, headers={"Content-Type": "application/json"})
    with urllib.request.urlopen(req, timeout=20) as r:
        return json.load(r)

def main(path):
    scenarios = json.load(open(path))
    scenarios = scenarios if isinstance(scenarios, list) else scenarios["scenarios"]
    failures = 0
    for s in scenarios:
        sid, words = s["id"], s["in_their_words"]
        language = "es" if sid == "C02" else "en"
        t0 = time.time()
        try:
            got = call(words, language)
        except Exception as e:
            print(f"{sid}  ERROR  {e}"); failures += 1; continue
        secs = time.time() - t0

        need, route = got.get("need"), got.get("route")
        want_need, want_route = EXPECT.get(sid, (None, None))
        bad = []
        if want_need and need != want_need: bad.append(f"need={need} want={want_need}")
        if want_route and route != want_route: bad.append(f"route={route} want={want_route}")
        mark = "FAIL" if bad else "ok  "
        if bad: failures += 1

        print(f"{mark} {sid}  {s['title'][:34]:<34} need={str(need):<28} route={route:<9} "
              f"constraints={','.join(got.get('constraints') or []) or '-':<22} {secs:.1f}s")
        if bad: print(f"       ^ {'; '.join(bad)}")
        print(f"       expected needs: {s.get('needs')}")
    print(f"\n{len(scenarios) - failures}/{len(scenarios)} passed")
    return 1 if failures else 0

if __name__ == "__main__":
    default = ("/private/tmp/claude-501/-Users-matthew-Code-hacks-claude-impact-lab-garcia-chok/"
               "c9903428-c522-49a0-bfde-968f20a65348/scratchpad/claude-impact-lab-oc/challenges/"
               "2-finding-the-right-resource/scenarios/client-scenarios.json")
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else default))
