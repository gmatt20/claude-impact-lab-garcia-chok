package org.bridge.app.domain

import org.bridge.app.api.SeedResource

/** How someone actually gets help from a record. Drives the primary action (the "locked door" rule). */
enum class AccessKind { REFERRAL, APPOINTMENT, PHONE, WALK_IN, ONLINE, MIXED, UNKNOWN }

fun accessKind(r: SeedResource): AccessKind = when {
    r.referralRequired == true || r.howToAccess == "referral-only" -> AccessKind.REFERRAL
    r.howToAccess == "appointment" -> AccessKind.APPOINTMENT
    r.howToAccess == "phone" -> AccessKind.PHONE
    r.howToAccess == "walk-in" -> AccessKind.WALK_IN
    r.howToAccess == "online" -> AccessKind.ONLINE
    r.howToAccess == "mixed" -> AccessKind.MIXED
    else -> AccessKind.UNKNOWN
}

private val ConfidentialTags = setOf("survivors", "domestic-violence", "dv", "confidential-location")

/**
 * Never show the address of a survivor-serving / confidential program, whatever the data holds.
 * The server sets addressWithheld; this is a second line of defense.
 */
fun isAddressWithheld(r: SeedResource, safetyMode: Boolean): Boolean = safetyMode || isConfidentialLocation(r)

/** Survivor-serving or confidential-location program, per the server flag or the record's own tags. */
fun isConfidentialLocation(r: SeedResource): Boolean =
    r.addressWithheld ||
        r.tags.any { it.lowercase() in ConfidentialTags } ||
        r.population.any { it.contains("domestic violence", ignoreCase = true) }

/** An address is a destination only when the record says people can walk in. Otherwise it may be a head office. */
fun addressIsDestination(r: SeedResource): Boolean = accessKind(r) == AccessKind.WALK_IN

const val CONSTRAINT_NO_PHONE = "no-phone"
const val CONSTRAINT_PHONE_HARD = "phone-hard"

fun cannotCall(constraints: List<String>) =
    CONSTRAINT_NO_PHONE in constraints || CONSTRAINT_PHONE_HARD in constraints
