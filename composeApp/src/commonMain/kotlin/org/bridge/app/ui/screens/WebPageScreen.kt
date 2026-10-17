package org.bridge.app.ui.screens

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import org.bridge.app.api.displayHost
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.InAppBrowser
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.PrimaryButton
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.rememberIsOnline

/**
 * A provider's website or source page, inside the app (mobile only).
 * Third-party pages are the one part of Bridge that needs the internet, so offline we say so
 * plainly instead of showing a blank page.
 */
@Composable
fun ColumnScope.WebPageScreen(strings: Strings, url: String) {
    val online = rememberIsOnline()
    var failed by remember(url) { mutableStateOf(false) }
    var attempt by remember(url) { mutableIntStateOf(0) }
    Body(displayHost(url), muted = true)
    if (!online || failed) {
        ScreenTitle(strings.pageNeedsInternet)
        Notice(if (online) strings.pageFailed else strings.pageNeedsInternetBody)
        Body(url)
        PrimaryButton(strings.tryAgain, { failed = false; attempt++ })
        return
    }
    key(url, attempt) {
        InAppBrowser(url, Modifier.fillMaxWidth().weight(1f), onLoadFailed = { failed = true })
    }
}
