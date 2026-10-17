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
import org.bridge.app.domain.CONSTRAINT_NO_PHONE
import org.bridge.app.domain.CONSTRAINT_PHONE_HARD
import org.bridge.app.domain.LoadedBundle
import org.bridge.app.domain.Match
import org.bridge.app.domain.MatchResult
import org.bridge.app.domain.matchResources
import org.bridge.app.i18n.Language
import org.bridge.app.i18n.Need

sealed interface BundleState {
    data object Loading : BundleState
    data object Unavailable : BundleState
    data class Ready(val loaded: LoadedBundle) : BundleState
}

sealed interface Screen {
    data object Welcome : Screen
    data object Need : Screen
    data object MoreNeeds : Screen
    data object City : Screen
    data object PhoneAccess : Screen
    data class FollowUp(val question: String) : Screen
    data object Results : Screen
    data class NextStep(val match: Match) : Screen
    data object HumanHelp : Screen
    data object Danger : Screen
    data object Clinical : Screen
    /** A third-party page shown in the in-app browser (mobile only). */
    data class WebPage(val url: String) : Screen
}

enum class PhoneAccess(val constraint: String?) { YES(null), HARD(CONSTRAINT_PHONE_HARD), NONE(CONSTRAINT_NO_PHONE) }

/** What the user has chosen so far. Kept in memory only; nothing about the user is persisted. */
data class Answers(
    val need: String? = null,
    val city: String? = null,
    /** Flat slugs, e.g. "no-phone". */
    val constraints: List<String> = emptyList(),
    val message: String = "",
    val followUpAnswer: String = "",
)

class AppState(private val api: BridgeApi?, private val scope: CoroutineScope) {
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
    /** Set when intake was unavailable; the need screen gently suggests tapping a tile instead. */
    var intakeFellBack by mutableStateOf(false)
        private set
    /** Set by the danger route: hide every address until Start over. */
    var safetyMode by mutableStateOf(false)
        private set
    /** Records the user said didn't work; hidden so they can try another. */
    var triedIds by mutableStateOf(setOf<String>())
        private set

    /** Free text needs the backend; without one we only offer the tiles. */
    val canInterpretText get() = api != null
    val isOffline get() = (bundle as? BundleState.Ready)?.loaded?.source == BundleSource.CACHE
    val current: Screen get() = stack.last()
    val canGoBack get() = stack.size > 1

    private var intakeJob: Job? = null

    init { loadBundle() }

    fun loadBundle() {
        bundle = BundleState.Loading
        scope.launch { bundle = repository.load()?.let { BundleState.Ready(it) } ?: BundleState.Unavailable }
    }

    /**
     * Called when the device regains a connection: swap in a fresh bundle if we were running from
     * the saved copy. Never blanks the screen; a failed refresh just keeps what we have.
     */
    fun refreshIfOffline() {
        if (!isOffline) return
        scope.launch {
            val fresh = repository.load()
            if (fresh?.source == BundleSource.NETWORK) bundle = BundleState.Ready(fresh)
        }
    }

    val results: MatchResult?
        get() {
            val b = (bundle as? BundleState.Ready)?.loaded?.bundle ?: return null
            val need = answers.need ?: return MatchResult.NoMatch
            return matchResources(b, need, answers.city, answers.constraints, triedIds)
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
        safetyMode = false
        stack = listOf(Screen.Welcome)
    }

    fun begin() = push(Screen.Need)
    fun showMoreNeeds() = push(Screen.MoreNeeds)
    fun showHumanHelp() = push(Screen.HumanHelp)

    fun chooseNeed(need: Need) {
        cancelIntake()
        answers = answers.copy(need = need.code)
        push(Screen.City)
    }

    fun chooseCity(city: String?) {
        answers = answers.copy(city = city)
        push(Screen.PhoneAccess)
    }

    fun choosePhoneAccess(access: PhoneAccess?) {
        val others = answers.constraints - setOf(CONSTRAINT_NO_PHONE, CONSTRAINT_PHONE_HARD)
        answers = answers.copy(constraints = others + listOfNotNull(access?.constraint))
        push(Screen.Results)
    }

    val phoneAccess: PhoneAccess?
        get() = PhoneAccess.entries.firstOrNull { it.constraint != null && it.constraint in answers.constraints }

    fun updateMessage(value: String) { answers = answers.copy(message = value.take(500)) }
    fun updateFollowUp(value: String) { answers = answers.copy(followUpAnswer = value.take(300)) }

    /**
     * Free-text path. Claude is a convenience: on timeout or any failure we quietly fall back
     * to the need tiles, with no error screen. The intake `route` drives safety handling.
     */
    fun submitMessage() {
        val client = api ?: return
        val text = listOf(answers.message, answers.followUpAnswer).filter { it.isNotBlank() }.joinToString("\n")
        if (text.isBlank()) return
        val askedFollowUp = current is Screen.FollowUp
        cancelIntake()
        interpreting = true
        intakeJob = scope.launch {
            val intake = runCatching {
                client.intake(IntakeRequest(text, language.code, IntakeContext(city = answers.city, need = answers.need)))
            }.getOrNull()
            interpreting = false
            if (intake == null) {
                intakeFellBack = true
                if (askedFollowUp) stack = stack.dropLast(1)
                return@launch
            }
            intakeFellBack = false
            val need = Need.fromCode(intake.need)?.code
            answers = answers.copy(
                need = need,
                city = intake.city ?: answers.city,
                constraints = (answers.constraints + intake.constraints).distinct(),
            )
            when {
                intake.route == "danger" -> { safetyMode = true; push(Screen.Danger) }
                intake.route == "clinical" -> push(Screen.Clinical)
                intake.route == "unknown" -> { answers = answers.copy(need = null); push(Screen.Results) }
                intake.needsHumanHelp -> push(Screen.HumanHelp)
                intake.followUpQuestion != null && !askedFollowUp -> push(Screen.FollowUp(intake.followUpQuestion))
                need == null -> { intakeFellBack = true; if (askedFollowUp) stack = stack.dropLast(1) }
                else -> push(Screen.Results)
            }
        }
    }

    fun skipFollowUp() = push(Screen.Results)

    /** From the clinical screen: back to choosing a need, keeping nothing from the question. */
    fun leaveClinical() {
        answers = answers.copy(message = "", followUpAnswer = "")
        stack = listOf(Screen.Welcome, Screen.Need)
    }

    fun chooseMatch(match: Match) = push(Screen.NextStep(match))

    fun openWebPage(url: String) = push(Screen.WebPage(url))

    /** "This didn't work": hide that record and return to the list. */
    fun matchDidNotWork(match: Match) {
        triedIds = triedIds + match.resource.id
        back()
    }

    private fun cancelIntake() {
        intakeJob?.cancel()
        interpreting = false
    }
}
