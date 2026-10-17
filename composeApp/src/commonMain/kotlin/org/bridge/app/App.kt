package org.bridge.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import org.bridge.app.api.BridgeApi
import org.bridge.app.api.HttpBridgeApi
import org.bridge.app.api.phoneNumbers
import org.bridge.app.domain.accessKind
import org.bridge.app.domain.cannotCall
import org.bridge.app.domain.fitExplanation
import org.bridge.app.domain.knownCities
import org.bridge.app.i18n.stringsFor
import org.bridge.app.ui.components.BridgeScaffold
import org.bridge.app.ui.components.LoadingRow
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.PrimaryButton
import org.bridge.app.ui.components.SecondaryButton
import org.bridge.app.ui.printCardHtml
import org.bridge.app.ui.screens.CityScreen
import org.bridge.app.ui.screens.ClinicalScreen
import org.bridge.app.ui.screens.DangerScreen
import org.bridge.app.ui.screens.FollowUpScreen
import org.bridge.app.ui.screens.HumanHelpScreen
import org.bridge.app.ui.screens.MoreNeedsScreen
import org.bridge.app.ui.screens.NeedScreen
import org.bridge.app.ui.screens.NextStepScreen
import org.bridge.app.ui.screens.PhoneAccessScreen
import org.bridge.app.ui.screens.ResultsScreen
import org.bridge.app.ui.screens.WelcomeScreen
import org.bridge.app.ui.state.AppState
import org.bridge.app.ui.state.BundleState
import org.bridge.app.ui.state.Screen
import org.bridge.app.ui.theme.BridgeColors
import org.bridge.app.ui.theme.BridgeTheme

/** The backend, if the page was opened with `?api=...`. Without one, the app runs on the bundled seed file. */
fun createApi(): BridgeApi? = configuredApiBaseUrl()?.let(::HttpBridgeApi)

@Composable
fun App(api: BridgeApi? = remember { createApi() }) {
    val scope = rememberCoroutineScope()
    val state = remember { AppState(api, scope) }
    val strings = stringsFor(state.language)
    val screen = state.current
    val noCalls = cannotCall(state.answers.constraints)

    BridgeTheme {
        BridgeScaffold(
            strings = strings,
            language = state.language,
            onLanguageChange = { state.language = it },
            showOffline = state.isOffline,
            onNeedHelp = if (screen != Screen.HumanHelp) state::showHumanHelp else null,
            onBack = if (state.canGoBack) state::back else null,
            onStartOver = if (screen != Screen.Welcome) state::startOver else null,
            screenKey = state.stack.size to screen,
        ) {
            when (screen) {
                Screen.Welcome -> WelcomeScreen(strings, state.language, { state.language = it }, state::begin)
                Screen.Need -> NeedScreen(
                    strings, state.answers.need, state.canInterpretText, state.answers.message,
                    state.interpreting, state.intakeFellBack,
                    state::chooseNeed, state::showMoreNeeds, state::updateMessage, state::submitMessage,
                )
                Screen.MoreNeeds -> MoreNeedsScreen(strings, state.answers.need, state::chooseNeed)
                Screen.PhoneAccess -> PhoneAccessScreen(strings, state.phoneAccess, state::choosePhoneAccess)
                is Screen.FollowUp -> FollowUpScreen(
                    strings, screen.question, state.answers.followUpAnswer, state.interpreting,
                    state::updateFollowUp, state::submitMessage, state::skipFollowUp,
                )
                Screen.HumanHelp -> HumanHelpScreen(strings, state::back)
                Screen.Danger -> DangerScreen(strings, state::back)
                Screen.Clinical -> ClinicalScreen(strings, state::leaveClinical)
                // Everything below renders from the bundle, with no network.
                Screen.City, Screen.Results, is Screen.NextStep -> when (val b = state.bundle) {
                    BundleState.Loading -> LoadingRow(strings.loadingResults)
                    BundleState.Unavailable -> {
                        Notice(strings.loadError, BridgeColors.Danger, BridgeColors.DangerSoft)
                        PrimaryButton(strings.tryAgain, state::loadBundle)
                        SecondaryButton(strings.askStaff, state::showHumanHelp)
                    }
                    is BundleState.Ready -> {
                        val bundle = b.loaded.bundle
                        when (screen) {
                            Screen.City -> CityScreen(strings, knownCities(bundle), state.answers.city, state::chooseCity)
                            Screen.Results -> ResultsScreen(
                                strings, bundle, state.results!!, state.safetyMode, noCalls,
                                state::chooseMatch, state::showHumanHelp,
                            )
                            is Screen.NextStep -> {
                                val r = screen.match.resource
                                val kind = accessKind(r)
                                val steps = strings.stepsFor(kind, phoneNumbers(r.phone).isNotEmpty(), noCalls)
                                val questions = strings.questionsFor(kind)
                                NextStepScreen(
                                    strings, screen.match, steps, questions,
                                    fitExplanation(bundle, screen.match, strings.language.code),
                                    state.safetyMode, noCalls,
                                    onPrint = { printHtml(printCardHtml(r, steps, questions, strings, state.safetyMode)) },
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
