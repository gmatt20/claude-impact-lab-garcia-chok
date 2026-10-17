package org.bridge.app.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bridge.app.api.BridgeApi
import org.bridge.app.api.IntakeContext
import org.bridge.app.api.IntakeRequest
import org.bridge.app.domain.BundleRepository
import org.bridge.app.domain.BundleSource
import org.bridge.app.domain.LoadedBundle
import org.bridge.app.domain.Match
import org.bridge.app.domain.MatchResult
import org.bridge.app.domain.matchResources
import org.bridge.app.i18n.Goal
import org.bridge.app.i18n.Language

sealed interface BundleState {
    data object Loading : BundleState
    data object Unavailable : BundleState
    data class Ready(val loaded: LoadedBundle) : BundleState
}

sealed interface Screen {
    data object Welcome : Screen
    data object Goal : Screen
    data object City : Screen
    data class FollowUp(val question: String) : Screen
    data object Results : Screen
    data class NextStep(val match: Match) : Screen
    data object HumanHelp : Screen
}

/** What the user has chosen so far. Kept in memory only; nothing about the user is persisted. */
data class Answers(
    val goal: String? = null,
    val city: String? = null,
    val message: String = "",
    val followUpAnswer: String = "",
    val constraints: List<String> = emptyList(),
)

class AppState(private val api: BridgeApi, private val scope: CoroutineScope) {
    private val repository = BundleRepository(api)

    var language by mutableStateOf(Language.EN)
    var answers by mutableStateOf(Answers())
        private set
    var stack by mutableStateOf(listOf<Screen>(Screen.Welcome))
        private set
    var bundle by mutableStateOf<BundleState>(BundleState.Loading)
        private set
    /** True while waiting (at most ~3s) on free-text intake. */
    var interpreting by mutableStateOf(false)
        private set
    /** Set when intake was unavailable; the Goal screen gently suggests tapping a category instead. */
    var intakeFellBack by mutableStateOf(false)
        private set
    /** Resources the user said didn't work; hidden so they can try another. */
    var triedIds by mutableStateOf(setOf<String>())
        private set

    val isMock get() = api.isMock
    val isOffline get() = (bundle as? BundleState.Ready)?.loaded?.source == BundleSource.CACHE
    val current: Screen get() = stack.last()
    val canGoBack get() = stack.size > 1

    private var intakeJob: Job? = null

    init { loadBundle() }

    fun loadBundle() {
        bundle = BundleState.Loading
        scope.launch { bundle = repository.load()?.let { BundleState.Ready(it) } ?: BundleState.Unavailable }
    }

    val results: MatchResult?
        get() {
            val b = (bundle as? BundleState.Ready)?.loaded?.bundle ?: return null
            val goal = answers.goal ?: return MatchResult.NoMatch
            return matchResources(b, goal, answers.city, triedIds)
        }

    private fun push(screen: Screen) { if (current != screen) stack = stack + screen }

    fun back() {
        if (!canGoBack) return
        cancelIntake()
        stack = stack.dropLast(1)
    }

    fun startOver() {
        cancelIntake()
        answers = Answers()
        triedIds = emptySet()
        intakeFellBack = false
        stack = listOf(Screen.Welcome)
    }

    fun begin() = push(Screen.Goal)
    fun showHumanHelp() = push(Screen.HumanHelp)

    fun chooseGoal(goal: Goal) {
        cancelIntake()
        answers = answers.copy(goal = goal.code, constraints = emptyList())
        push(Screen.City)
    }

    fun chooseCity(city: String?) {
        answers = answers.copy(city = city)
        push(Screen.Results)
    }

    fun updateMessage(value: String) { answers = answers.copy(message = value.take(500)) }
    fun updateFollowUp(value: String) { answers = answers.copy(followUpAnswer = value.take(300)) }

    /**
     * Free-text path. Claude is a convenience: on timeout or any failure we quietly
     * fall back to the goal tiles, with no error screen.
     */
    fun submitMessage() {
        val text = listOf(answers.message, answers.followUpAnswer).filter { it.isNotBlank() }.joinToString("\n")
        if (text.isBlank()) return
        val askedFollowUp = current is Screen.FollowUp
        cancelIntake()
        interpreting = true
        intakeJob = scope.launch {
            val intake = runCatching {
                api.intake(IntakeRequest(text, language.code, IntakeContext(city = answers.city, goal = answers.goal)))
            }.getOrNull()
            interpreting = false
            if (intake == null) {
                intakeFellBack = true
                if (askedFollowUp) stack = stack.dropLast(1)
                return@launch
            }
            intakeFellBack = false
            answers = answers.copy(
                goal = intake.need.takeIf { n -> Goal.entries.any { it.code == n } } ?: Goal.OTHER.code,
                city = intake.city ?: answers.city,
                constraints = intake.constraints,
            )
            when {
                intake.needsHumanHelp -> push(Screen.HumanHelp)
                intake.followUpQuestion != null && !askedFollowUp -> push(Screen.FollowUp(intake.followUpQuestion))
                else -> push(Screen.Results)
            }
        }
    }

    fun skipFollowUp() = push(Screen.Results)

    fun chooseMatch(match: Match) = push(Screen.NextStep(match))

    /** "This didn't work": hide that resource and return to the list. */
    fun matchDidNotWork(match: Match) {
        triedIds = triedIds + match.resource.id
        back()
    }

    private fun cancelIntake() {
        intakeJob?.cancel()
        interpreting = false
    }
}
