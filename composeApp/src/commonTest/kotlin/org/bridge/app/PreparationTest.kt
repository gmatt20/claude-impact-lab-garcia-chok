package org.bridge.app

import org.bridge.app.api.Bundle
import org.bridge.app.api.BridgeJson
import org.bridge.app.domain.preparationFor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** "Before you go" is served by the backend, so the wire shape has to stay in step with it. */
class PreparationTest {

    // Trimmed from a real GET /api/bundle response.
    private val wire = """
        {
          "resources": [],
          "preparation": {
            "bridges-at-kraemer-place": {
              "en": {
                "fits": ["Are you an adult?", "Are you experiencing homelessness right now?"],
                "bring": [],
                "headsUp": "You'll need a referral to get in. You can't just show up."
              },
              "es": { "fits": ["¿Eres adulto?"], "bring": [], "headsUp": null }
            },
            "oc-general-relief": {
              "es": { "fits": ["¿Vives en el Condado de Orange?"], "bring": [], "headsUp": null }
            }
          }
        }
    """.trimIndent()

    private val bundle = BridgeJson.decodeFromString(Bundle.serializer(), wire)

    @Test
    fun parsesTheBackendWireShape() {
        val prep = preparationFor(bundle, "bridges-at-kraemer-place", "en")!!
        assertEquals(2, prep.fits.size)
        assertTrue(prep.bring.isEmpty())
        assertEquals("You'll need a referral to get in. You can't just show up.", prep.headsUp)
    }

    @Test
    fun usesTheReadersLanguage() {
        assertEquals("¿Eres adulto?", preparationFor(bundle, "bridges-at-kraemer-place", "es")!!.fits.single())
    }

    /**
     * A row held back in review can leave a language with nothing approved. Falling back to
     * English is right; falling back to a language the reader cannot read is not. Show nothing.
     */
    @Test
    fun doesNotFallBackToALanguageTheReaderCannotRead() {
        assertNull(preparationFor(bundle, "oc-general-relief", "en"))
        assertEquals(
            "¿Vives en el Condado de Orange?",
            preparationFor(bundle, "oc-general-relief", "es")!!.fits.single(),
        )
    }

    @Test
    fun missingResourceIsNullNotAnError() {
        assertNull(preparationFor(bundle, "no-such-resource", "en"))
    }

    /** The seed file has no preparation at all; the offline path must still work. */
    @Test
    fun bundleWithoutPreparationIsFine() {
        val seedOnly = BridgeJson.decodeFromString(Bundle.serializer(), """{"resources":[]}""")
        assertNull(preparationFor(seedOnly, "anything", "en"))
    }
}
