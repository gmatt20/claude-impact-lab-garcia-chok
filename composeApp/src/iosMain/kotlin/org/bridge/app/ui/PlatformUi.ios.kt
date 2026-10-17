package org.bridge.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberIsOnline(): Boolean {
    // Optimistic until the first path update arrives (immediately after start).
    var online by remember { mutableStateOf(true) }
    DisposableEffect(Unit) {
        val monitor = nw_path_monitor_create()
        nw_path_monitor_set_update_handler(monitor) { path ->
            online = nw_path_get_status(path) == nw_path_status_satisfied
        }
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
        onDispose { nw_path_monitor_cancel(monitor) }
    }
    return online
}

/**
 * Only http(s) navigation is allowed inside the page: tel:, mailto: and app links are cancelled,
 * so a website can never place a call or open another app on its own.
 */
private class PageDelegate(private val onLoadFailed: () -> Unit) : NSObject(), WKNavigationDelegateProtocol {
    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit,
    ) {
        val scheme = decidePolicyForNavigationAction.request.URL?.scheme
        decisionHandler(
            if (scheme == "https" || scheme == "http") WKNavigationActionPolicy.WKNavigationActionPolicyAllow
            else WKNavigationActionPolicy.WKNavigationActionPolicyCancel,
        )
    }

    override fun webView(webView: WKWebView, didFailProvisionalNavigation: WKNavigation?, withError: NSError) {
        onLoadFailed()
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun InAppBrowser(url: String, modifier: Modifier, onLoadFailed: () -> Unit) {
    // WKWebView holds its navigation delegate weakly, so keep a strong reference here.
    val delegate = remember(url) { PageDelegate(onLoadFailed) }
    UIKitView(
        factory = {
            WKWebView(frame = CGRectZero.readValue(), configuration = WKWebViewConfiguration()).apply {
                navigationDelegate = delegate
                NSURL.URLWithString(url)?.let { loadRequest(NSURLRequest.requestWithURL(it)) }
            }
        },
        modifier = modifier,
    )
}

/** iOS has no system back button; the on-screen Back button covers it. */
@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
