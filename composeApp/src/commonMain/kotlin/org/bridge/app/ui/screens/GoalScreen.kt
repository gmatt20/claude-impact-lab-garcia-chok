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
import org.bridge.app.i18n.Goal
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ChoiceButton
import org.bridge.app.ui.components.LoadingRow
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton
import org.bridge.app.ui.theme.BridgeColors

/**
 * Goal tiles are the primary, always-offline path. Free text is an optional shortcut
 * that needs the network; if it fails we just ask the user to tap a tile.
 */
@Composable
fun GoalScreen(
    strings: Strings,
    selected: String?,
    message: String,
    interpreting: Boolean,
    fellBack: Boolean,
    onChoose: (Goal) -> Unit,
    onMessage: (String) -> Unit,
    onSubmitMessage: () -> Unit,
) {
    ScreenTitle(strings.goalTitle)
    Body(strings.goalHint, muted = true)
    Goal.entries.forEach { goal ->
        ChoiceButton(strings.goalLabel(goal), goal.code == selected) { onChoose(goal) }
    }
    if (fellBack) Notice(strings.intakeFallback, BridgeColors.Primary, BridgeColors.PrimarySoft)
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
