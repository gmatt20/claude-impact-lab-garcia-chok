package org.bridge.app

import org.bridge.app.ui.printableDocument
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIMarkupTextPrintFormatter
import platform.UIKit.UIPrintInfo
import platform.UIKit.UIPrintInfoOutputType
import platform.UIKit.UIPrintInteractionController

actual fun configuredApiBaseUrl(): String? = DEFAULT_API_BASE_URL

actual fun readCache(key: String): String? =
    runCatching { NSUserDefaults.standardUserDefaults.stringForKey(key) }.getOrNull()

actual fun writeCache(key: String, value: String) {
    runCatching { NSUserDefaults.standardUserDefaults.setObject(value, forKey = key) }
}

/** The system print sheet, which also offers Save to Files (PDF) and AirDrop. */
actual fun printHtml(cardHtml: String) {
    val controller = UIPrintInteractionController.sharedPrintController
    controller.printInfo = UIPrintInfo.printInfo().apply {
        jobName = "Bridge card"
        outputType = UIPrintInfoOutputType.UIPrintInfoOutputGeneral
    }
    controller.printFormatter = UIMarkupTextPrintFormatter(markupText = printableDocument(cardHtml))
    controller.presentAnimated(true, completionHandler = null)
}

actual val hasInAppBrowser: Boolean = true
