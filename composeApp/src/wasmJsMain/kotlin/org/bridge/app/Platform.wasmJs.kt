@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package org.bridge.app

import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

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

actual fun printHtml(cardHtml: String) {
    val root = document.getElementById("print-root") ?: return
    // Content is escaped by printCardHtml; values come only from curated records and UI copy.
    // index.html's print stylesheet hides the app and shows only this element.
    root.innerHTML = cardHtml
    window.print()
}

/** On the web, third-party pages open in a new tab; many sites refuse to be framed anyway. */
actual val hasInAppBrowser: Boolean = false
