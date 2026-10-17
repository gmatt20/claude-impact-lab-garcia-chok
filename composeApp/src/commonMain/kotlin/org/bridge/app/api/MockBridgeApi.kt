package org.bridge.app.api

import kotlinx.coroutines.delay

/**
 * DEVELOPMENT MOCK ONLY. These records are schema placeholders, NOT real services.
 * The UI shows a visible "Demo data" banner whenever this is in use.
 * Replace with HttpBridgeApi (backend verified records) before any demo to users.
 */
class MockBridgeApi : BridgeApi {
    override val isMock = true

    override suspend fun bundle(): Bundle {
        delay(300)
        return MockBundle
    }

    override suspend fun intake(request: IntakeRequest): IntakeResponse {
        delay(500)
        val text = request.message.lowercase()
        val city = MockBundle.cities.firstOrNull { text.contains(it.lowercase()) }
            ?: if (text.contains("tokyo")) "Tokyo" else request.context.city
        val need = when {
            listOf("sleep", "tonight", "shelter", "kicked out", "dormir").any(text::contains) -> "shelter"
            listOf("food", "eat", "hungry", "comida").any(text::contains) -> "food"
            listOf("doctor", "clinic", "medic", "salud").any(text::contains) -> "health"
            listOf("job", "work", "trabajo").any(text::contains) -> "work"
            else -> request.context.goal ?: "other"
        }
        return IntakeResponse(
            need = need,
            city = city,
            constraints = listOfNotNull("no-car".takeIf { text.contains("no car") }),
            needsHumanHelp = listOf("hurt", "unsafe", "emergency", "peligro").any(text::contains),
        )
    }
}

private fun mockResource(id: String, category: String, area: List<String>, phone: String?, web: String?) =
    BundleResource(
        id = id,
        name = "[MOCK] Example $category resource ($id)",
        categories = listOf(category),
        serviceArea = area,
        description = mapOf("en" to "Placeholder. Not a real service.", "es" to "Ejemplo. No es un servicio real."),
        phone = phone,
        website = web,
        eligibilityNotes = mapOf("en" to "Must confirm with provider.", "es" to "Hay que confirmar con el proveedor."),
        sourceUrl = "https://example.org",
        lastVerified = "2026-10-17",
    )

private val MockBundle = Bundle(
    version = "mock-1",
    cities = listOf("Anaheim", "Santa Ana", "Orange", "Fullerton", "Garden Grove"),
    resources = listOf(
        mockResource("m-shelter-1", "shelter", listOf("Anaheim"), "000-000-0000", null),
        mockResource("m-shelter-2", "shelter", listOf("*"), null, "https://example.org"),
        mockResource("m-food-1", "food", listOf("Santa Ana", "Anaheim"), "000-000-0000", "https://example.org"),
        mockResource("m-health-1", "health", listOf("*"), "000-000-0000", null),
        mockResource("m-work-1", "work", listOf("Orange", "Fullerton"), null, "https://example.org"),
    ),
    explanations = mapOf(
        "m-shelter-1" to mapOf("shelter" to mapOf("en" to "Placeholder: offers shelter in Anaheim.", "es" to "Ejemplo: ofrece albergue en Anaheim.")),
    ),
    nextSteps = mapOf(
        "shelter" to NextStepTemplate(
            title = mapOf("en" to "Your next step", "es" to "Tu próximo paso"),
            steps = listOf(mapOf("en" to "Call and ask if they have space tonight.", "es" to "Llama y pregunta si tienen espacio esta noche.")),
            questionsToAsk = listOf(
                mapOf("en" to "Do you have space tonight?", "es" to "¿Tienen espacio esta noche?"),
                mapOf("en" to "What time should I arrive?", "es" to "¿A qué hora debo llegar?"),
                mapOf("en" to "What should I bring?", "es" to "¿Qué debo traer?"),
            ),
        ),
    ),
    disclaimer = mapOf(
        "en" to "Confirm eligibility and availability directly.",
        "es" to "Confirma la elegibilidad y disponibilidad directamente.",
    ),
)
