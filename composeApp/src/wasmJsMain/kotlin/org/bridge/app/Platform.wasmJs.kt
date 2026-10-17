@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package org.bridge.app

import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.Response
import org.w3c.dom.url.URLSearchParams

private fun isoDateJs(): String = js("new Date().toISOString().slice(0, 10)")

actual fun todayIso(): String? = runCatching { isoDateJs() }.getOrNull()

/**
 * Supabase Edge Function that serves /api/bundle and /api/intake.
 * Safe to ship in the bundle: it is a public URL, and row level security - not secrecy -
 * is what protects the data. No API key of any kind reaches the client.
 */
private const val DEFAULT_API_BASE_URL = "https://ntgqdwfpkbmzodgygbxi.supabase.co/functions/v1"

actual fun configuredApiBaseUrl(): String? {
    val fromQuery = URLSearchParams(window.location.search.toJsString()).get("api")
    // `?api=off` forces the bundled-seed path, for testing offline behaviour with wifi on.
    if (fromQuery == "off") return null
    return fromQuery?.takeIf { it.startsWith("http://") || it.startsWith("https://") }
        ?: DEFAULT_API_BASE_URL
}

actual fun readCache(key: String): String? = runCatching { localStorage.getItem(key) }.getOrNull()

actual fun writeCache(key: String, value: String) {
    runCatching { localStorage.setItem(key, value) }
}

actual suspend fun fetchBundledText(path: String): String? = runCatching {
    val response = window.fetch(path).await<Response>()
    if (!response.ok) null else response.text().await<JsString>().toString()
}.getOrNull()

actual fun printHtml(html: String) {
    val root = document.getElementById("print-root") ?: return
    // Content is escaped by printCardHtml; values come only from curated records and UI copy.
    root.innerHTML = html
    window.print()
}
