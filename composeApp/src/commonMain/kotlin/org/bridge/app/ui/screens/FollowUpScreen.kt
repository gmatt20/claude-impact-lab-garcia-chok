package org.bridge.app.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.LoadingRow
import org.bridge.app.ui.components.PrimaryButton
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton

/** The one follow-up question intake may ask. Always skippable. */
@Composable
fun FollowUpScreen(
    strings: Strings,
    question: String,
    answer: String,
    interpreting: Boolean,
    onAnswer: (String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    ScreenTitle(question)
    Body(strings.followUpOptional, muted = true)
    OutlinedTextField(
        value = answer,
        onValueChange = onAnswer,
        label = { Text(question) },
        minLines = 2,
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth(),
    )
    if (interpreting) LoadingRow(strings.interpreting)
    else PrimaryButton(strings.continueLabel, onContinue, enabled = answer.isNotBlank())
    SecondaryButton(strings.skip, onSkip)
}
