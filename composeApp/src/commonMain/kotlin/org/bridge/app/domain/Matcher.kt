package org.bridge.app.domain

import org.bridge.app.api.Bundle
import org.bridge.app.api.BundleResource
import org.bridge.app.api.pick

/** Why a resource was shown. Rendered in plain language under "Why am I seeing this?". */
sealed interface MatchReason {
    data class Offers(val goal: String) : MatchReason
    data class ServesCity(val city: String) : MatchReason
    data object ServesWholeArea : MatchReason
}

data class Match(
    val resource: BundleResource,
    val goal: String,
    val reasons: List<MatchReason>,
)

sealed interface MatchResult {
    data class Found(val matches: List<Match>) : MatchResult
    /** The city is outside what the catalog covers (e.g. "Tokyo"). */
    data class CityNotCovered(val city: String) : MatchResult
    data object NoMatch : MatchResult
}

const val MAX_RESULTS = 3

/**
 * Deterministic, offline matching against curated records only.
 * Category must match; a known city narrows results, and city-specific records rank above region-wide ones.
 */
fun matchResources(bundle: Bundle, goal: String, city: String?, excludeIds: Set<String> = emptySet()): MatchResult {
    val wantedCity = city?.trim()?.takeIf { it.isNotEmpty() }
    val knownCity = wantedCity?.let { c -> bundle.cities.firstOrNull { it.equals(c, ignoreCase = true) } }
    if (wantedCity != null && knownCity == null && bundle.cities.isNotEmpty()) {
        return MatchResult.CityNotCovered(wantedCity)
    }
    val matches = bundle.resources
        .filter { goal in it.categories && it.id !in excludeIds }
        .mapNotNull { r ->
            val wholeArea = "*" in r.serviceArea
            val servesCity = knownCity != null && r.serviceArea.any { it.equals(knownCity, ignoreCase = true) }
            when {
                knownCity == null -> Match(r, goal, listOf(MatchReason.Offers(goal), areaReason(r)))
                servesCity -> Match(r, goal, listOf(MatchReason.Offers(goal), MatchReason.ServesCity(knownCity!!)))
                wholeArea -> Match(r, goal, listOf(MatchReason.Offers(goal), MatchReason.ServesWholeArea))
                else -> null
            }
        }
        .sortedBy { m -> if (m.reasons.any { it is MatchReason.ServesCity }) 0 else 1 }
        .take(MAX_RESULTS)
    return if (matches.isEmpty()) MatchResult.NoMatch else MatchResult.Found(matches)
}

private fun areaReason(r: BundleResource): MatchReason =
    if ("*" in r.serviceArea || r.serviceArea.isEmpty()) MatchReason.ServesWholeArea
    else MatchReason.ServesCity(r.serviceArea.joinToString(", "))

fun fitExplanation(bundle: Bundle, match: Match, language: String): String? =
    bundle.explanations[match.resource.id]?.get(match.goal)?.pick(language)

/** A next step in the user's language, from the resource's template, else the goal's, else null. */
data class Plan(
    val title: String?,
    val steps: List<String>,
    val questionsToAsk: List<String>,
    val alternatives: List<String>,
)

fun planFor(bundle: Bundle, match: Match, language: String): Plan? {
    val t = bundle.nextSteps[match.resource.id] ?: bundle.nextSteps[match.goal] ?: return null
    return Plan(
        title = t.title.pick(language),
        steps = t.steps.mapNotNull { it.pick(language) },
        questionsToAsk = t.questionsToAsk.mapNotNull { it.pick(language) },
        alternatives = t.alternatives.mapNotNull { it.pick(language) },
    )
}
