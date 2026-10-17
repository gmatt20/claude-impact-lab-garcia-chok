package org.bridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bridge.app.i18n.Language
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ChoiceButton
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.PrimaryButton
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SectionTitle

@Composable
fun WelcomeScreen(strings: Strings, language: Language, onLanguage: (Language) -> Unit, onStart: () -> Unit) {
    ScreenTitle(strings.welcomeTitle)
    Body(strings.welcomeBody)
    SectionTitle(strings.chooseLanguage)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Language.entries.forEach { lang ->
            Row(Modifier.weight(1f)) { ChoiceButton(lang.label, lang == language) { onLanguage(lang) } }
        }
    }
    PrimaryButton(strings.start, onStart)
    Notice(strings.welcomeDisclaimer)
}
