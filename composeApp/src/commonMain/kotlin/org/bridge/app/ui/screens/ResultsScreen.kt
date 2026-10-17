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
    language: String,
    bundle: Bundle,
    result: MatchResult,
    today: String?,
    onChoose: (Match) -> Unit,
    onHelp: () -> Unit,
) {
    when (result) {
        is MatchResult.Found -> {
            ScreenTitle(strings.resultsTitle)
            result.matches.forEach { match ->
                ResourceCard(match, fitExplanation(bundle, match, language), strings, language, today, showContacts = false) {
                    PrimaryButton(strings.chooseThis, { onChoose(match) })
                }
            }
            SecondaryButton(strings.askCaseworker, onHelp)
        }
        // Explicit, calm no-match: we never invent a provider to fill the gap.
        is MatchResult.CityNotCovered -> NoMatch(strings, strings.cityNotCovered(result.city), onHelp)
        MatchResult.NoMatch -> NoMatch(strings, null, onHelp)
    }
}

@Composable
private fun NoMatch(strings: Strings, detail: String?, onHelp: () -> Unit) {
    ScreenTitle(strings.resultsNone)
    detail?.let { Body(it) }
    Body(strings.resultsNoneBody)
    PrimaryButton(strings.askCaseworker, onHelp)
}
