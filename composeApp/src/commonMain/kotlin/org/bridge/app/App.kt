package org.bridge.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import org.bridge.app.api.BridgeApi
import org.bridge.app.api.HttpBridgeApi
import org.bridge.app.api.MockBridgeApi
import org.bridge.app.domain.Plan
import org.bridge.app.domain.fitExplanation
import org.bridge.app.domain.planFor
import org.bridge.app.i18n.Strings
import org.bridge.app.i18n.stringsFor
import org.bridge.app.ui.components.BridgeScaffold
import org.bridge.app.ui.components.LoadingRow
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.PrimaryButton
import org.bridge.app.ui.printCardHtml
import org.bridge.app.ui.screens.CityScreen
import org.bridge.app.ui.screens.FollowUpScreen
import org.bridge.app.ui.screens.GoalScreen
import org.bridge.app.ui.screens.HumanHelpScreen
import org.bridge.app.ui.screens.NextStepScreen
import org.bridge.app.ui.screens.ResultsScreen
import org.bridge.app.ui.screens.WelcomeScreen
import org.bridge.app.ui.state.AppState
import org.bridge.app.ui.state.BundleState
import org.bridge.app.ui.state.Screen
import org.bridge.app.ui.theme.BridgeColors
import org.bridge.app.ui.theme.BridgeTheme

fun createApi(): BridgeApi = configuredApiBaseUrl()?.let(::HttpBridgeApi) ?: MockBridgeApi()

@Composable
fun App(api: BridgeApi = remember { createApi() }) {
    val scope = rememberCoroutineScope()
    val state = remember { AppState(api, scope) }
    val today = remember { todayIso() }
    val strings = stringsFor(state.language)
    val lang = state.language.code
    val screen = state.current

    BridgeTheme {
        BridgeScaffold(
            strings = strings,
            language = state.language,
            onLanguageChange = { state.language = it },
            showMockBanner = state.isMock,
            showOffline = state.isOffline,
            onNeedHelp = if (screen != Screen.HumanHelp) state::showHumanHelp else null,
            onBack = if (state.canGoBack) state::back else null,
            onStartOver = if (screen != Screen.Welcome) state::startOver else null,
            screenKey = state.stack.size to screen,
        ) {
            when (screen) {
                Screen.Welcome -> WelcomeScreen(strings, state.language, { state.language = it }, state::begin)
                Screen.Goal -> GoalScreen(
                    strings, state.answers.goal, state.answers.message, state.interpreting, state.intakeFellBack,
                    state::chooseGoal, state::updateMessage, state::submitMessage,
                )
                is Screen.FollowUp -> FollowUpScreen(
                    strings, screen.question, state.answers.followUpAnswer, state.interpreting,
                    state::updateFollowUp, state::submitMessage, state::skipFollowUp,
                )
                Screen.HumanHelp -> HumanHelpScreen(strings, state::back)
                // Everything below renders from the cached bundle, with no network.
                Screen.City, Screen.Results, is Screen.NextStep -> when (val b = state.bundle) {
                    BundleState.Loading -> LoadingRow(strings.loadingSaved)
                    BundleState.Unavailable -> {
                        Notice(strings.bundleUnavailable, BridgeColors.Danger, BridgeColors.DangerSoft)
                        PrimaryButton(strings.tryAgain, state::loadBundle)
                    }
                    is BundleState.Ready -> {
                        val bundle = b.loaded.bundle
                        when (screen) {
                            Screen.City -> CityScreen(strings, bundle.cities, state.answers.city, state::chooseCity)
                            Screen.Results -> ResultsScreen(
                                strings, lang, bundle, state.results!!, today, state::chooseMatch, state::showHumanHelp,
                            )
                            is Screen.NextStep -> {
                                val plan = planFor(bundle, screen.match, lang) ?: defaultPlan(strings)
                                NextStepScreen(
                                    strings, lang, screen.match, plan, fitExplanation(bundle, screen.match, lang), today,
                                    onPrint = { printHtml(printCardHtml(screen.match, plan, strings, lang)) },
                                    onDidNotWork = { state.matchDidNotWork(screen.match) },
                                    onHelp = state::showHumanHelp,
                                )
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

private fun defaultPlan(strings: Strings) = Plan(
    title = null,
    steps = listOf(strings.defaultStep),
    questionsToAsk = strings.defaultQuestions,
    alternatives = emptyList(),
)
