package org.bridge.app.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Client mirror of the contract in BRIDGE_SHARED_CONTEXT.md. Field names on the wire match the
// event seed file exactly. When the backend publishes its `shared` models, switch to those.

/** Text in each supported language, keyed by language code ("en", "es"). */
typealias Localized = Map<String, String>

fun Localized.pick(language: String): String? = this[language] ?: this["en"]

// ---- POST /api/intake (network-only; the app works without it) ----

@Serializable
data class IntakeContext(
    val city: String? = null,
    val need: String? = null,
)

@Serializable
data class IntakeRequest(
    val message: String,
    val language: String,
    val context: IntakeContext,
)

@Serializable
data class IntakeResponse(
    val need: String? = null,
    val city: String? = null,
    /** Flat slugs, e.g. "no-phone". */
    val constraints: List<String> = emptyList(),
    val followUpQuestion: String? = null,
    /** "normal" | "danger" | "clinical" | "unknown" */
    val route: String = "normal",
    val needsHumanHelp: Boolean = false,
)

// ---- GET /api/bundle ----
// The event seed file ({generated, disclaimer, resources}) is itself a valid Bundle,
// which is what the app falls back to when no backend is reachable.

@Serializable
data class Bundle(
    val generated: String? = null,
    val disclaimer: String? = null,
    val resources: List<SeedResource> = emptyList(),
    /** resourceId -> need -> pre-generated, human-reviewed fit explanation. Optional. */
    val explanations: Map<String, Map<String, Localized>> = emptyMap(),
)

/** One record from oc-resources.seed.json, plus the server's optional additions. */
@Serializable
data class SeedResource(
    val id: String,
    val name: String,
    val organization: String? = null,
    val category: String,
    val tags: List<String> = emptyList(),
    val description: String? = null,
    val services: List<String> = emptyList(),
    val eligibility: String? = null,
    val population: List<String> = emptyList(),
    @SerialName("how_to_access") val howToAccess: String? = null,
    @SerialName("referral_required") val referralRequired: Boolean? = null,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val hours: String? = null,
    val languages: List<String>? = null,
    val cost: String? = null,
    val website: String? = null,
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("source_quote") val sourceQuote: String? = null,
    @SerialName("verified_date") val verifiedDate: String? = null,
    val confidence: String? = null,
    val notes: String? = null,
    // Server additions from /api/match (absent in the raw seed file).
    val fitExplanation: String? = null,
    val addressWithheld: Boolean = false,
)
