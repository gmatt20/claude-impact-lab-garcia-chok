package org.bridge.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import org.bridge.app.api.SeedResource
import org.bridge.app.api.displayHost
import org.bridge.app.api.phoneNumbers
import org.bridge.app.api.safeWebUrl
import org.bridge.app.domain.AccessKind
import org.bridge.app.domain.Match
import org.bridge.app.domain.MatchReason
import org.bridge.app.domain.accessKind
import org.bridge.app.domain.addressIsDestination
import org.bridge.app.domain.isAddressWithheld
import org.bridge.app.domain.isConfidentialLocation
import org.bridge.app.i18n.Language
import org.bridge.app.i18n.Strings
import org.bridge.app.i18n.formatDate
import org.bridge.app.i18n.needLabel
import org.bridge.app.ui.theme.BridgeColors

/**
 * A seed record, rendered field by field. Null is the normal case in this data
 * (half the records have no address), so every field is optional and nothing is filled in.
 */
@Composable
fun ResourceCard(
    match: Match,
    fitExplanation: String?,
    strings: Strings,
    safetyMode: Boolean,
    cannotCall: Boolean,
    detailed: Boolean,
    footer: @Composable ColumnScope.() -> Unit,
) {
    val r = match.resource
    val kind = accessKind(r)
    Column(
        Modifier.fillMaxWidth()
            .background(BridgeColors.Surface, RoundedCornerShape(14.dp))
            .border(1.dp, BridgeColors.Border.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(r.name, style = MaterialTheme.typography.titleLarge, color = BridgeColors.Ink, modifier = Modifier.semantics { heading() })
        r.organization?.takeIf { it.isNotBlank() && it != r.name }?.let { Body(it, muted = true) }

        Label(strings.howToGetHelp)
        Body(strings.accessExplain(kind))
        if (r.referralRequired == null && kind != AccessKind.REFERRAL) Body(strings.referralUnknown, muted = true)

        fitExplanation?.takeIf { it.isNotBlank() }?.let { Label(strings.whyFit); Body(it) }

        if (detailed) Details(r, strings, safetyMode, kind, cannotCall)

        ConfidenceNote(r, strings)
        Notice(strings.availabilityUnknown)
        strings.formatDate(r.verifiedDate)?.let { Body(strings.checkedOn(it), muted = true) }
        WhyAmISeeingThis(match, strings)
        footer()
    }
}

@Composable
private fun Details(r: SeedResource, strings: Strings, safetyMode: Boolean, kind: AccessKind, cannotCall: Boolean) {
    if (strings.language == Language.ES) Body(strings.sourceInEnglish, muted = true)
    r.description?.takeIf { it.isNotBlank() }?.let { Body(it) }
    // Requirements are shown uncut; summarising could hide one.
    r.eligibility?.takeIf { it.isNotBlank() }?.let { Label(strings.eligibility); Body(it) }
    r.cost?.takeIf { it.isNotBlank() }?.let { Label(strings.cost); Body(it) }
    r.hours?.takeIf { it.isNotBlank() }?.let { Label(strings.hours); Body(it) }
    r.languages?.takeIf { it.isNotEmpty() }?.let { Label(strings.languages); Body(it.joinToString(", ")) }
    ContactActions(r, strings, kind, cannotCall)
    AddressBlock(r, strings, safetyMode)
}

/** Primary action follows how_to_access, never the address (the locked-door rule). */
@Composable
private fun ContactActions(r: SeedResource, strings: Strings, kind: AccessKind, cannotCall: Boolean) {
    val phones = phoneNumbers(r.phone)
    val website = safeWebUrl(r.website)
    // Show the phone text verbatim, so numbers we can't turn into a button (e.g. 1-800-RUNAWAY) still appear.
    r.phone?.takeIf { it.isNotBlank() }?.let { Label(strings.phoneLabel); Body(it) }
    val callIsPrimary = !cannotCall && kind in setOf(AccessKind.PHONE, AccessKind.APPOINTMENT, AccessKind.MIXED, AccessKind.UNKNOWN)
    val webIsPrimary = !callIsPrimary && kind in setOf(AccessKind.ONLINE, AccessKind.APPOINTMENT, AccessKind.MIXED, AccessKind.UNKNOWN)
    phones.forEachIndexed { i, p ->
        ExternalActionButton(strings.call(p.display), ExternalTarget(p.tel, strings.confirmCall(p.display)), strings, primary = callIsPrimary && i == 0)
    }
    website?.let { url ->
        val host = displayHost(url)
        ExternalActionButton(strings.openWebsite(host), ExternalTarget(url, strings.confirmOpen(host)), strings, primary = webIsPrimary)
    }
}

@Composable
private fun AddressBlock(r: SeedResource, strings: Strings, safetyMode: Boolean) {
    if (isAddressWithheld(r, safetyMode)) {
        if (r.address != null || isConfidentialLocation(r)) Notice(strings.addressWithheld)
        return
    }
    val address = r.address?.takeIf { it.isNotBlank() } ?: return
    Label(strings.address)
    Body(address)
    when {
        accessKind(r) == AccessKind.REFERRAL -> Notice(strings.addressNeedsReferral)
        !addressIsDestination(r) -> Body(strings.addressOffice, muted = true)
    }
}

@Composable
private fun ConfidenceNote(r: SeedResource, strings: Strings) {
    when (r.confidence) {
        "medium" -> Notice(strings.confidenceMedium)
        "low" -> Notice(strings.confidenceLow, BridgeColors.Danger, BridgeColors.DangerSoft)
        else -> Unit
    }
}

/** The source, verbatim: quote, link, checked date, notes. Grounding made visible. */
@Composable
private fun WhyAmISeeingThis(match: Match, strings: Strings) {
    val r = match.resource
    var open by rememberSaveable(r.id) { mutableStateOf(false) }
    NavTextButton(if (open) strings.hideWhy else strings.whyAmISeeing, { open = !open })
    AnimatedVisibility(open) {
        Column(
            Modifier.fillMaxWidth()
                .background(BridgeColors.PrimarySoft, RoundedCornerShape(10.dp))
                .padding(14.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            match.reasons.forEach { Body("• " + reasonText(it, strings)) }
            r.sourceQuote?.takeIf { it.isNotBlank() }?.let {
                Label(strings.sourceSays)
                Text("“$it”", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic, color = BridgeColors.Ink)
            }
            safeWebUrl(r.sourceUrl)?.let { url ->
                ExternalActionButton(strings.sourceLink(displayHost(url)), ExternalTarget(url, strings.confirmOpen(displayHost(url))), strings)
            }
            strings.formatDate(r.verifiedDate)?.let { Body(strings.checkedOn(it)) }
            ConfidenceNote(r, strings)
            r.notes?.takeIf { it.isNotBlank() }?.let { Label(strings.notesTitle); Body(it, muted = true) }
        }
    }
}

private fun reasonText(reason: MatchReason, strings: Strings): String = when (reason) {
    is MatchReason.Offers -> strings.reasonOffers(needLabel(reason.need, strings.language))
    is MatchReason.InYourCity -> strings.reasonInYourCity(reason.city)
    is MatchReason.InOtherCity -> strings.reasonInOtherCity(reason.city)
    MatchReason.NotTiedToCity -> strings.reasonNotTiedToCity
    MatchReason.NoPhoneNeeded -> strings.reasonNoPhoneNeeded
}

@Composable
private fun Label(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = BridgeColors.Ink)
}
