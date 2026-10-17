@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package org.bridge.app

import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

private fun isoDateJs(): String = js("new Date().toISOString().slice(0, 10)")

actual fun todayIso(): String? = runCatching { isoDateJs() }.getOrNull()

actual fun configuredApiBaseUrl(): String? {
    val fromQuery = URLSearchParams(window.location.search.toJsString()).get("api")
    return fromQuery?.takeIf { it.startsWith("http://") || it.startsWith("https://") }
}

actual fun readCache(key: String): String? = runCatching { localStorage.getItem(key) }.getOrNull()

actual fun writeCache(key: String, value: String) {
    runCatching { localStorage.setItem(key, value) }
}

actual fun printHtml(html: String) {
    val root = document.getElementById("print-root") ?: return
    // Content is escaped by printCardHtml; values come only from curated records and UI copy.
    root.innerHTML = html
    window.print()
}
