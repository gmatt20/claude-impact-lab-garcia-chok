package org.bridge.app.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bridge.app.i18n.Need
import org.bridge.app.i18n.Strings
import org.bridge.app.i18n.needLabel
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ChoiceButton
import org.bridge.app.ui.components.LoadingRow
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton
import org.bridge.app.ui.theme.BridgeColors

/**
 * IHH's four named needs lead; the other categories sit behind "Something else".
 * Tiles are the always-offline path. Free text is an optional shortcut that needs the backend.
 */
@Composable
fun NeedScreen(
    strings: Strings,
    selected: String?,
    showFreeText: Boolean,
    message: String,
    interpreting: Boolean,
    fellBack: Boolean,
    onChoose: (Need) -> Unit,
    onSomethingElse: () -> Unit,
    onMessage: (String) -> Unit,
    onSubmitMessage: () -> Unit,
) {
    ScreenTitle(strings.needTitle)
    Body(strings.needHint, muted = true)
    Need.headline.forEach { need ->
        ChoiceButton(needLabel(need, strings.language), need.code == selected) { onChoose(need) }
    }
    ChoiceButton(strings.somethingElse, selected != null && Need.headline.none { it.code == selected }, onSomethingElse)
    if (fellBack) Notice(strings.intakeFallback, BridgeColors.Primary, BridgeColors.PrimarySoft)
    if (!showFreeText) return
    HorizontalDivider(color = BridgeColors.Border.copy(alpha = 0.4f))
    OutlinedTextField(
        value = message,
        onValueChange = onMessage,
        label = { Text(strings.describeLabel) },
        placeholder = { Text(strings.describeHint) },
        minLines = 2,
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
    )
    if (interpreting) LoadingRow(strings.interpreting)
    else if (message.isNotBlank()) SecondaryButton(strings.describeSubmit, onSubmitMessage)
}

@Composable
fun MoreNeedsScreen(strings: Strings, selected: String?, onChoose: (Need) -> Unit) {
    ScreenTitle(strings.moreNeedsTitle)
    Need.more.forEach { need ->
        ChoiceButton(needLabel(need, strings.language), need.code == selected) { onChoose(need) }
    }
}
