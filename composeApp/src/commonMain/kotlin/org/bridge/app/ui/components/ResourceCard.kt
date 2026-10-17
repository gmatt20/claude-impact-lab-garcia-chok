package org.bridge.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import org.bridge.app.api.displayHost
import org.bridge.app.api.pick
import org.bridge.app.api.safeWebUrl
import org.bridge.app.api.telUri
import org.bridge.app.domain.Match
import org.bridge.app.domain.MatchReason
import org.bridge.app.i18n.Goal
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.theme.BridgeColors

/** Records older than this many days get a "may have changed" note. */
private const val STALE_AFTER_DAYS = 90

@Composable
fun ResourceCard(
    match: Match,
    fitExplanation: String?,
    strings: Strings,
    language: String,
    today: String?,
    showContacts: Boolean,
    footer: @Composable () -> Unit,
) {
    val r = match.resource
    Column(
        Modifier.fillMaxWidth()
            .background(BridgeColors.Surface, RoundedCornerShape(14.dp))
            .border(1.dp, BridgeColors.Border.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            r.name,
            style = MaterialTheme.typography.titleLarge,
            color = BridgeColors.Ink,
            modifier = Modifier.semantics { heading() },
        )
        r.description?.pick(language)?.takeIf { it.isNotBlank() }?.let { Body(it) }
        fitExplanation?.takeIf { it.isNotBlank() }?.let {
            Text(strings.whyFit, style = MaterialTheme.typography.titleMedium, color = BridgeColors.Primary)
            Body(it)
        }
        r.eligibilityNotes?.pick(language)?.takeIf { it.isNotBlank() }?.let {
            Text(strings.eligibility, style = MaterialTheme.typography.titleMedium, color = BridgeColors.Ink)
            Body(it)
        }
        if (r.availability != "confirmed") Notice(strings.availabilityUnknown)
        if (showContacts) ContactActions(match, strings)
        r.lastVerified?.let { Body(strings.lastChecked(it), muted = true) }
        if (isStale(r.lastVerified, today)) Notice(strings.notRecentlyChecked)
        WhyAmISeeingThis(match, strings)
        footer()
    }
}

@Composable
private fun ContactActions(match: Match, strings: Strings) {
    val r = match.resource
    telUri(r.phone)?.let { uri ->
        ExternalActionButton(strings.call(r.phone!!), ExternalTarget(uri, strings.confirmCall(r.phone)), strings, primary = true)
    }
    safeWebUrl(r.website)?.let { url ->
        val host = displayHost(url)
        ExternalActionButton(strings.openWebsite(host), ExternalTarget(url, strings.confirmOpen(host)), strings)
    }
    r.address?.takeIf { it.isNotBlank() }?.let {
        Text(strings.address, style = MaterialTheme.typography.titleMedium, color = BridgeColors.Ink)
        Body(it)
    }
}

/** Plain-language disclosure of what drove the match and where the record came from. */
@Composable
private fun WhyAmISeeingThis(match: Match, strings: Strings) {
    var open by rememberSaveable(match.resource.id) { mutableStateOf(false) }
    NavTextButton(if (open) strings.hideWhy else strings.whyAmISeeing, { open = !open })
    AnimatedVisibility(open) {
        Column(
            Modifier.fillMaxWidth()
                .background(BridgeColors.PrimarySoft, RoundedCornerShape(10.dp))
                .padding(14.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            match.reasons.forEach { Body("• " + reasonText(it, strings)) }
            val source = safeWebUrl(match.resource.sourceUrl)
            Body("• " + (source?.let(strings.reasonSource) ?: strings.reasonNoSource))
            match.resource.lastVerified?.let { Body("• " + strings.reasonChecked(it)) }
        }
    }
}

private fun reasonText(reason: MatchReason, strings: Strings): String = when (reason) {
    is MatchReason.Offers -> strings.reasonOffers(
        Goal.entries.firstOrNull { it.code == reason.goal }?.let(strings.goalLabel) ?: reason.goal,
    )
    is MatchReason.ServesCity -> strings.reasonCity(reason.city)
    MatchReason.ServesWholeArea -> strings.reasonWholeArea
}

/** Compares ISO dates (yyyy-mm-dd). A missing or unparseable date counts as stale. */
internal fun isStale(lastVerified: String?, today: String?): Boolean {
    val verified = lastVerified?.let(::epochDay) ?: return true
    val now = today?.let(::epochDay) ?: return false
    return now - verified > STALE_AFTER_DAYS
}

private fun epochDay(iso: String): Long? {
    val p = iso.take(10).split('-').mapNotNull { it.toIntOrNull() }
    if (p.size != 3) return null
    val (y, m, d) = p
    // Days-from-civil (Howard Hinnant); enough for comparing dates.
    val yy = if (m <= 2) y - 1 else y
    val era = (if (yy >= 0) yy else yy - 399) / 400
    val yoe = yy - era * 400
    val doy = (153 * (m + (if (m > 2) -3 else 9)) + 2) / 5 + d - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return era * 146097L + doe - 719468
}
