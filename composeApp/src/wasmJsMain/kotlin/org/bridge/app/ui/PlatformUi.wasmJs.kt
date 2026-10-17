package org.bridge.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// The web build never shows the in-app browser (hasInAppBrowser = false), and the browser
// has its own Back button, so these are intentionally inert.

@Composable
actual fun rememberIsOnline(): Boolean = true

@Composable
actual fun InAppBrowser(url: String, modifier: Modifier, onLoadFailed: () -> Unit) = Unit

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
