package org.bridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bridge.app.api.Preparation
import org.bridge.app.domain.Match
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ResourceCard
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton
import org.bridge.app.ui.components.SectionTitle

@Composable
fun NextStepScreen(
    strings: Strings,
    match: Match,
    steps: List<String>,
    questions: List<String>,
    fitExplanation: String?,
    preparation: Preparation?,
    safetyMode: Boolean,
    cannotCall: Boolean,
    onPrint: () -> Unit,
    onDidNotWork: () -> Unit,
    onHelp: () -> Unit,
) {
    ScreenTitle(strings.nextStepTitle)
    NumberedList(steps)
    // Contact details come only from the record, never from template text.
    ResourceCard(match, fitExplanation, strings, safetyMode, cannotCall, detailed = true) {}
    SectionTitle(strings.whatToAsk)
    BulletList(questions)
    BeforeYouGo(preparation, strings)
    SecondaryButton(strings.printCard, onPrint)
    SecondaryButton(strings.didntWork, onDidNotWork)
    SecondaryButton(strings.askStaff, onHelp)
}

/**
 * What a caseworker would tell you before you contact this place. Every line was written
 * from the record's own eligibility and notes and approved by a person; nothing is generated
 * on the device. Sections are omitted when nothing has been approved for them.
 */
@Composable
private fun BeforeYouGo(preparation: Preparation?, strings: Strings) {
    val prep = preparation ?: return
    if (prep.fits.isEmpty() && prep.bring.isEmpty() && prep.headsUp.isNullOrBlank()) return

    ScreenTitle(strings.beforeYouGo)
    if (prep.fits.isNotEmpty()) {
        SectionTitle(strings.doesThisFit)
        BulletList(prep.fits)
    }
    if (prep.bring.isNotEmpty()) {
        SectionTitle(strings.whatToBring)
        BulletList(prep.bring)
    }
    prep.headsUp?.takeIf { it.isNotBlank() }?.let {
        SectionTitle(strings.headsUpTitle)
        Notice(it)
    }
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
