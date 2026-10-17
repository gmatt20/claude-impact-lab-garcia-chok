package org.bridge.app

import org.bridge.app.api.SeedResource
import org.bridge.app.api.phoneNumbers
import org.bridge.app.domain.AccessKind
import org.bridge.app.domain.CONSTRAINT_NO_PHONE
import org.bridge.app.domain.MatchResult
import org.bridge.app.domain.accessKind
import org.bridge.app.domain.addressIsDestination
import org.bridge.app.domain.isAddressWithheld
import org.bridge.app.domain.matchResources
import org.bridge.app.api.Bundle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SafetyRulesTest {
    private fun record(
        id: String = "r",
        category: String = "shelter",
        howToAccess: String? = "mixed",
        referral: Boolean? = false,
        phone: String? = null,
        address: String? = null,
        city: String? = null,
        tags: List<String> = emptyList(),
        population: List<String> = emptyList(),
    ) = SeedResource(
        id = id, name = id, category = category, howToAccess = howToAccess, referralRequired = referral,
        phone = phone, address = address, city = city, tags = tags, population = population,
    )

    @Test
    fun phoneNumbersAreTakenVerbatimFromFreeText() {
        assertEquals(listOf("tel:211", "tel:8886004357"), phoneNumbers("2-1-1 or 888-600-4357 (toll-free)").map { it.tel })
        assertEquals(listOf("tel:7145423577"), phoneNumbers("(714)542-3577").map { it.tel })
        assertEquals(listOf("tel:6198848467"), phoneNumbers("619.884.8467").map { it.tel })
        assertEquals(listOf("tel:18007862929"), phoneNumbers("1-800-RUNAWAY (1-800-786-2929)").map { it.tel })
        assertEquals(listOf("tel:988"), phoneNumbers("988").map { it.tel })
        assertTrue(phoneNumbers(null).isEmpty())
        assertTrue(phoneNumbers("Call the front desk").isEmpty())
    }

    @Test
    fun referralRequiredWinsOverHowToAccess() {
        assertEquals(AccessKind.REFERRAL, accessKind(record(howToAccess = "mixed", referral = true)))
        assertEquals(AccessKind.REFERRAL, accessKind(record(howToAccess = "referral-only", referral = null)))
        assertEquals(AccessKind.WALK_IN, accessKind(record(howToAccess = "walk-in")))
    }

    @Test
    fun addressIsOnlyADestinationForWalkIn() {
        assertTrue(addressIsDestination(record(howToAccess = "walk-in", address = "1 Main St")))
        assertFalse(addressIsDestination(record(howToAccess = "referral-only", referral = true, address = "1 Main St")))
        assertFalse(addressIsDestination(record(howToAccess = "mixed", address = "1 Main St")))
    }

    @Test
    fun survivorProgramAddressesAreAlwaysWithheld() {
        assertTrue(isAddressWithheld(record(tags = listOf("women", "survivors"), address = "x"), safetyMode = false))
        assertTrue(isAddressWithheld(record(population = listOf("survivors of domestic violence")), safetyMode = false))
        assertTrue(isAddressWithheld(record(address = "x"), safetyMode = true))
        assertTrue(isAddressWithheld(record(address = "x").copy(addressWithheld = true), safetyMode = false))
        assertFalse(isAddressWithheld(record(address = "x"), safetyMode = false))
    }

    @Test
    fun noPhoneClientIsNotOfferedPhoneOnlyPlacesFirst() {
        val bundle = Bundle(
            resources = listOf(
                record(id = "phone-only", category = "phone-internet", howToAccess = "phone", phone = "800-000-0000"),
                record(id = "online", category = "phone-internet", howToAccess = "online"),
            ),
        )
        val result = matchResources(bundle, "phone-internet", null, listOf(CONSTRAINT_NO_PHONE)) as MatchResult.Found
        assertEquals("online", result.matches.first().resource.id)
    }

    @Test
    fun unknownNeedIsAnExplicitNoMatch() {
        val bundle = Bundle(resources = listOf(record()))
        assertEquals(MatchResult.NoMatch, matchResources(bundle, "hotel-in-tokyo", null, emptyList()))
    }
}
