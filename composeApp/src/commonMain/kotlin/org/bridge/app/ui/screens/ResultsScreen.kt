package org.bridge.app.ui.screens

import androidx.compose.runtime.Composable
import org.bridge.app.api.Bundle
import org.bridge.app.domain.Match
import org.bridge.app.domain.MatchResult
import org.bridge.app.domain.fitExplanation
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.PrimaryButton
import org.bridge.app.ui.components.ResourceCard
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton

@Composable
fun ResultsScreen(
    strings: Strings,
    bundle: Bundle,
    result: MatchResult,
    safetyMode: Boolean,
    cannotCall: Boolean,
    onChoose: (Match) -> Unit,
    onHelp: () -> Unit,
) {
    when (result) {
        is MatchResult.Found -> {
            ScreenTitle(strings.resultsTitle)
            result.matches.forEach { match ->
                ResourceCard(
                    match, fitExplanation(bundle, match, strings.language.code), strings,
                    safetyMode, cannotCall, detailed = false,
                ) {
                    PrimaryButton(strings.chooseThis, { onChoose(match) })
                }
            }
            SecondaryButton(strings.askStaff, onHelp)
        }
        // Explicit, calm no-match: we never invent a provider to fill the gap.
        MatchResult.NoMatch -> {
            ScreenTitle(strings.resultsNone)
            Body(strings.resultsNoneBody)
            PrimaryButton(strings.askStaff, onHelp)
        }
    }
}
