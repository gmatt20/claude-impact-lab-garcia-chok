// Bridge service worker: keeps the app shell available offline.
// Same-origin GETs are served network-first with a cache fallback, so a fresh deploy is
// picked up when online and the last good copy is used when offline. The resource bundle
// itself is also stored in localStorage by the app (works even if this worker is unavailable).
const CACHE = "bridge-shell-v3";
const SHELL = ["./", "index.html", "composeApp.js", "manifest.webmanifest", "composeResources/org.bridge.app.resources/files/oc-resources.seed.json"];

self.addEventListener("install", (event) => {
  event.waitUntil(caches.open(CACHE).then((c) => c.addAll(SHELL)).then(() => self.skipWaiting()));
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k))))
      .then(() => self.clients.claim()),
  );
});

self.addEventListener("fetch", (event) => {
  const req = event.request;
  if (req.method !== "GET" || new URL(req.url).origin !== self.location.origin) return;
  event.respondWith(
    fetch(req)
      .then((res) => {
        if (res.ok) {
          const copy = res.clone();
          caches.open(CACHE).then((c) => c.put(req, copy));
        }
        return res;
      })
      .catch(() => caches.match(req, { ignoreSearch: true }).then((hit) => hit || Response.error())),
  );
});
