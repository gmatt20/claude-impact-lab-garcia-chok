package org.bridge.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import org.bridge.app.ui.printableDocument
import java.lang.ref.WeakReference

/** Holds the Android context the platform functions need. Set by MainActivity. */
internal object AndroidPlatform {
    private var activity = WeakReference<Activity>(null)
    var prefs: SharedPreferences? = null
        private set

    /** Kept alive until the print job is handed to the system, then released. */
    @SuppressLint("StaticFieldLeak")
    var printWebView: WebView? = null

    fun attach(activity: Activity) {
        this.activity = WeakReference(activity)
        prefs = activity.applicationContext.getSharedPreferences("bridge", Context.MODE_PRIVATE)
    }

    fun detach(activity: Activity) {
        if (this.activity.get() === activity) this.activity = WeakReference(null)
    }

    fun currentActivity(): Activity? = activity.get()
}

actual fun configuredApiBaseUrl(): String? = DEFAULT_API_BASE_URL

actual fun readCache(key: String): String? = runCatching { AndroidPlatform.prefs?.getString(key, null) }.getOrNull()

actual fun writeCache(key: String, value: String) {
    runCatching { AndroidPlatform.prefs?.edit()?.putString(key, value)?.apply() }
}

/** Renders the card in an off-screen WebView and hands it to the system print dialog (print or save as PDF). */
actual fun printHtml(cardHtml: String) {
    val activity = AndroidPlatform.currentActivity() ?: return
    val webView = WebView(activity)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            val printManager = activity.getSystemService(Context.PRINT_SERVICE) as PrintManager
            printManager.print(
                "Bridge card",
                view.createPrintDocumentAdapter("Bridge card"),
                PrintAttributes.Builder().build(),
            )
            AndroidPlatform.printWebView = null
        }
    }
    AndroidPlatform.printWebView = webView
    webView.loadDataWithBaseURL(null, printableDocument(cardHtml), "text/html", "utf-8", null)
}

actual val hasInAppBrowser: Boolean = true
