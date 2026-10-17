package org.bridge.app.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun rememberIsOnline(): Boolean {
    val context = LocalContext.current
    val manager = remember { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    var online by remember { mutableStateOf(manager.hasInternet()) }
    DisposableEffect(manager) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { online = manager.hasInternet() }
            override fun onLost(network: Network) { online = manager.hasInternet() }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) { online = manager.hasInternet() }
        }
        runCatching { manager.registerDefaultNetworkCallback(callback) }
        onDispose { runCatching { manager.unregisterNetworkCallback(callback) } }
    }
    return online
}

private fun ConnectivityManager.hasInternet(): Boolean {
    val caps = getNetworkCapabilities(activeNetwork ?: return false) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

/**
 * Third-party page in a WebView. Only http(s) navigation is allowed inside it: tel:, mailto:, intents
 * and the like are ignored, so a website can never place a call or open another app on its own.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun InAppBrowser(url: String, modifier: Modifier, onLoadFailed: () -> Unit) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true // most provider sites need it
                settings.domStorageEnabled = true
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val scheme = request.url.scheme
                        return scheme != "https" && scheme != "http"
                    }

                    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                        if (request.isForMainFrame) onLoadFailed()
                    }
                }
                loadUrl(url)
            }
        },
        onRelease = { it.destroy() },
    )
}

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}
