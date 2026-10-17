package org.bridge.app

/** Today's date as yyyy-mm-dd, or null if unknown. */
expect fun todayIso(): String?

/**
 * Backend base URL. Defaults to the deployed Supabase Edge Function; override with
 * `?api=https://...` for a local backend, or `?api=off` to force the bundled-seed path.
 * Null means run entirely from the seed file shipped with the app.
 */
expect fun configuredApiBaseUrl(): String?

/** On-device key/value storage. May be unavailable (private mode); callers must tolerate failure. */
expect fun readCache(key: String): String?
expect fun writeCache(key: String, value: String)

/** Reads a file shipped with the app (served next to index.html), or null. */
expect suspend fun fetchBundledText(path: String): String?

/** Print a plain-HTML one-page card (Compose canvas content does not print well). */
expect fun printHtml(html: String)
