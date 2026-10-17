package org.bridge.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Whether the device currently has a network connection. Used only to decide if a web page can load. */
@Composable
expect fun rememberIsOnline(): Boolean

/** A platform web view (Android WebView / iOS WKWebView). Only called where hasInAppBrowser is true. */
@Composable
expect fun InAppBrowser(url: String, modifier: Modifier, onLoadFailed: () -> Unit)

/** System back (Android back gesture/button). No-op where the platform has none. */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
