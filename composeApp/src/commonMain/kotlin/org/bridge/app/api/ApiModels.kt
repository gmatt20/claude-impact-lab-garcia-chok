package org.bridge.app.api

import kotlinx.serialization.Serializable

// Mirrors the contract in BRIDGE_SHARED_CONTEXT.md. When the backend publishes its ":shared"
// DTO module, delete this file and depend on that instead.

/** Text in each supported language, keyed by language code ("en", "es"). */
typealias Localized = Map<String, String>

fun Localized.pick(language: String): String? = this[language] ?: this["en"]

// ---- POST /api/intake (network-only; the app works without it) ----

@Serializable
data class IntakeContext(
    val city: String? = null,
    val goal: String? = null,
)

@Serializable
data class IntakeRequest(
    val message: String,
    val language: String,
    val context: IntakeContext,
)

@Serializable
data class IntakeResponse(
    val need: String,
    val city: String? = null,
    val constraints: List<String> = emptyList(),
    val followUpQuestion: String? = null,
    val needsHumanHelp: Boolean = false,
)

// ---- GET /api/bundle (PROPOSED shape — confirm with backend) ----
// One cacheable payload; every screen except free-text intake renders from it offline.

@Serializable
data class Bundle(
    val version: String,
    /** Cities the catalog covers. A city outside this list is an explicit no-match. */
    val cities: List<String> = emptyList(),
    val resources: List<BundleResource> = emptyList(),
    /** resourceId -> goal -> pre-generated, human-reviewed fit explanation. */
    val explanations: Map<String, Map<String, Localized>> = emptyMap(),
    /** Keyed by resourceId first, then by goal as a fallback. */
    val nextSteps: Map<String, NextStepTemplate> = emptyMap(),
    val disclaimer: Localized = emptyMap(),
)

@Serializable
data class BundleResource(
    val id: String,
    val name: String,
    /** Goal codes this resource serves: shelter, food, health, work, other. */
    val categories: List<String> = emptyList(),
    /** City names served; "*" means anywhere in the catalog's region. */
    val serviceArea: List<String> = emptyList(),
    val description: Localized? = null,
    val phone: String? = null,
    val website: String? = null,
    val address: String? = null,
    val eligibilityNotes: Localized? = null,
    val availability: String = "unknown",
    val sourceUrl: String? = null,
    val lastVerified: String? = null,
)

@Serializable
data class NextStepTemplate(
    val title: Localized = emptyMap(),
    val steps: List<Localized> = emptyList(),
    val questionsToAsk: List<Localized> = emptyList(),
    val alternatives: List<Localized> = emptyList(),
)
