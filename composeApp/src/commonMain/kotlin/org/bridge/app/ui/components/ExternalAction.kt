package org.bridge.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalUriHandler
import org.bridge.app.i18n.Strings

/**
 * Opens a web page inside the app, where the platform has an in-app browser (mobile).
 * Null on the web, where pages open in a new tab through the system URI handler.
 */
val LocalInAppPageOpener = staticCompositionLocalOf<((String) -> Unit)?> { null }

/** A pending call or link the user must confirm before it happens. */
data class ExternalTarget(val uri: String, val description: String)

/**
 * Button that asks for confirmation before calling or opening a link.
 * Never auto-contacts anyone: the user always confirms first.
 */
@Composable
fun ExternalActionButton(
    label: String,
    target: ExternalTarget,
    strings: Strings,
    primary: Boolean = false,
) {
    var pending by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val openInApp = LocalInAppPageOpener.current
    if (primary) PrimaryButton(label, { pending = true }) else SecondaryButton(label, { pending = true })
    if (pending) {
        AlertDialog(
            onDismissRequest = { pending = false },
            title = { Text(strings.confirmLeaveTitle) },
            text = { Text(target.description) },
            confirmButton = {
                NavTextButton(strings.confirm, {
                    pending = false
                    val isWebPage = target.uri.startsWith("https://") || target.uri.startsWith("http://")
                    if (isWebPage && openInApp != null) openInApp(target.uri)
                    else runCatching { uriHandler.openUri(target.uri) }
                })
            },
            dismissButton = { NavTextButton(strings.cancel, { pending = false }) },
        )
    }
}
