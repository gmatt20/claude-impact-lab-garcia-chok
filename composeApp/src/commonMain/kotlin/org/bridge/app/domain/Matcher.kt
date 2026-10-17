package org.bridge.app.domain

import org.bridge.app.api.Bundle
import org.bridge.app.api.Preparation
import org.bridge.app.api.SeedResource
import org.bridge.app.api.pick

/** Why a record was shown. Rendered in plain language under "Why am I seeing this?". */
sealed interface MatchReason {
    data class Offers(val need: String) : MatchReason
    data class InYourCity(val city: String) : MatchReason
    data class InOtherCity(val city: String) : MatchReason
    data object NotTiedToCity : MatchReason
    data object NoPhoneNeeded : MatchReason
}

data class Match(
    val resource: SeedResource,
    val need: String,
    val reasons: List<MatchReason>,
)

sealed interface MatchResult {
    data class Found(val matches: List<Match>) : MatchResult
    data object NoMatch : MatchResult
}

const val MAX_RESULTS = 3

/**
 * Deterministic, offline matching against seed records only:
 * category first, then city, then the client's phone constraint, then confidence.
 */
fun matchResources(
    bundle: Bundle,
    need: String,
    city: String?,
    constraints: List<String>,
    excludeIds: Set<String> = emptySet(),
): MatchResult {
    val noCalls = cannotCall(constraints)
    val matches = bundle.resources
        .filter { it.category == need && it.id !in excludeIds }
        .map { r ->
            val reasons = buildList {
                add(MatchReason.Offers(need))
                when {
                    r.city == null -> add(MatchReason.NotTiedToCity)
                    city != null && r.city.equals(city, ignoreCase = true) -> add(MatchReason.InYourCity(r.city))
                    else -> add(MatchReason.InOtherCity(r.city))
                }
                if (noCalls && accessKind(r) != AccessKind.PHONE) add(MatchReason.NoPhoneNeeded)
            }
            val score = (if (reasons.any { it is MatchReason.InYourCity }) 4 else 0) +
                (if (r.city == null) 2 else 0) +
                (if (noCalls && accessKind(r) == AccessKind.PHONE) -6 else 0) +
                (if (r.confidence == "high") 1 else 0)
            Match(r, need, reasons) to score
        }
        .sortedByDescending { it.second }
        .take(MAX_RESULTS)
        .map { it.first }
    return if (matches.isEmpty()) MatchResult.NoMatch else MatchResult.Found(matches)
}

/** Reviewed fit text, if the bundle or server supplied one. Never generated on the device. */
fun fitExplanation(bundle: Bundle, match: Match, language: String): String? =
    bundle.explanations[match.resource.id]?.get(match.need)?.pick(language) ?: match.resource.fitExplanation

/**
 * "Before you go" for a resource, in the reader's language, falling back to English.
 * Null when nothing has been written and approved for it yet.
 */
fun preparationFor(bundle: Bundle, resourceId: String, language: String): Preparation? {
    val byLanguage = bundle.preparation[resourceId] ?: return null
    return byLanguage[language] ?: byLanguage["en"]
}

/** Cities that appear in the records, for the optional city question. */
fun knownCities(bundle: Bundle): List<String> =
    bundle.resources.mapNotNull { it.city }.distinct().sorted()
