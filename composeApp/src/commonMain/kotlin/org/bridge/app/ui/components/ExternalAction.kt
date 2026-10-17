package org.bridge.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import org.bridge.app.i18n.Strings

/** A pending call or link the user must confirm before Bridge leaves the page. */
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
    if (primary) PrimaryButton(label, { pending = true }) else SecondaryButton(label, { pending = true })
    if (pending) {
        AlertDialog(
            onDismissRequest = { pending = false },
            title = { Text(strings.confirmLeaveTitle) },
            text = { Text(target.description) },
            confirmButton = {
                NavTextButton(strings.confirm, {
                    pending = false
                    runCatching { uriHandler.openUri(target.uri) }
                })
            },
            dismissButton = { NavTextButton(strings.cancel, { pending = false }) },
        )
    }
}
