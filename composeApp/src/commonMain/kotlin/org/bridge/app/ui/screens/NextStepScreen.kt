package org.bridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bridge.app.domain.Match
import org.bridge.app.domain.Plan
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ResourceCard
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton
import org.bridge.app.ui.components.SectionTitle

@Composable
fun NextStepScreen(
    strings: Strings,
    language: String,
    match: Match,
    plan: Plan,
    fitExplanation: String?,
    today: String?,
    onPrint: () -> Unit,
    onDidNotWork: () -> Unit,
    onHelp: () -> Unit,
) {
    ScreenTitle(plan.title ?: strings.nextStepTitle)
    NumberedList(plan.steps)
    // Contact details always come from the resource record, never from template text.
    ResourceCard(match, fitExplanation, strings, language, today, showContacts = true) {}
    if (plan.questionsToAsk.isNotEmpty()) {
        SectionTitle(strings.whatToAsk)
        BulletList(plan.questionsToAsk)
    }
    if (plan.alternatives.isNotEmpty()) {
        SectionTitle(strings.otherOptions)
        BulletList(plan.alternatives)
    }
    SecondaryButton(strings.printCard, onPrint)
    SecondaryButton(strings.didntWork, onDidNotWork)
    SecondaryButton(strings.askCaseworker, onHelp)
}

@Composable
private fun NumberedList(items: List<String>) = Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items.forEachIndexed { i, item ->
        Row { Text("${i + 1}.", Modifier.padding(end = 10.dp)); Body(item) }
    }
}

@Composable
private fun BulletList(items: List<String>) = Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items.forEach { item ->
        Row { Text("•", Modifier.padding(end = 10.dp)); Body(item) }
    }
}
