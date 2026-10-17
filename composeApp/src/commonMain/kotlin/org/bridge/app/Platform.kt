package org.bridge.app

/**
 * Supabase Edge Function that serves /api/bundle and /api/intake.
 * Safe to ship in the app: it is a public URL, and row level security - not secrecy -
 * is what protects the data. No API key of any kind reaches the client.
 */
const val DEFAULT_API_BASE_URL = "https://ntgqdwfpkbmzodgygbxi.supabase.co/functions/v1"

/**
 * Backend base URL. Null means run entirely from the seed file shipped with the app.
 * Web: defaults to [DEFAULT_API_BASE_URL]; `?api=https://...` overrides it and `?api=off` forces the seed path.
 * Mobile: always [DEFAULT_API_BASE_URL]; with no connection the app falls back to cache, then seed.
 */
expect fun configuredApiBaseUrl(): String?

/** On-device key/value storage. May be unavailable; callers must tolerate failure. */
expect fun readCache(key: String): String?
expect fun writeCache(key: String, value: String)

/** Print the one-page card. [cardHtml] is escaped HTML from printCardHtml. */
expect fun printHtml(cardHtml: String)

/**
 * True where third-party pages (provider websites, sources) open inside the app.
 * Mobile shows them in a WebView when online; the web build opens a new tab instead.
 */
expect val hasInAppBrowser: Boolean
