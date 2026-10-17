package org.bridge.app.ui

import org.bridge.app.api.SeedResource
import org.bridge.app.domain.AccessKind
import org.bridge.app.domain.accessKind
import org.bridge.app.domain.addressIsDestination
import org.bridge.app.domain.isAddressWithheld
import org.bridge.app.i18n.Strings
import org.bridge.app.i18n.formatDate

/** One-page printable card as plain HTML: name, how to access, phone, what to ask, source and checked date. */
fun printCardHtml(r: SeedResource, steps: List<String>, questions: List<String>, strings: Strings, safetyMode: Boolean): String {
    fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
    return buildString {
        append("<article class=\"card\" lang=\"${strings.language.code}\">")
        append("<p class=\"brand\">Bridge</p>")
        append("<h1>${esc(r.name)}</h1>")
        append("<p><strong>${esc(strings.howToGetHelp)}:</strong> ${esc(strings.accessExplain(accessKind(r)))}</p>")
        r.phone?.let { append("<p class=\"big\">☎ ${esc(it)}</p>") }
        r.hours?.let { append("<p>${esc(strings.hours)}: ${esc(it)}</p>") }
        if (!isAddressWithheld(r, safetyMode)) {
            r.address?.let {
                append("<p>${esc(strings.address)}: ${esc(it)}")
                val caveat = if (accessKind(r) == AccessKind.REFERRAL) strings.addressNeedsReferral else strings.addressOffice
                if (!addressIsDestination(r)) append("<br><span class=\"small\">${esc(caveat)}</span>")
                append("</p>")
            }
        }
        r.website?.let { append("<p>${esc(it)}</p>") }
        append("<h2>${esc(strings.nextStepTitle)}</h2><ol>")
        steps.forEach { append("<li>${esc(it)}</li>") }
        append("</ol>")
        append("<h2>${esc(strings.whatToAsk)}</h2><ul>")
        questions.forEach { append("<li>☐ ${esc(it)}</li>") }
        append("</ul>")
        r.eligibility?.let { append("<p>${esc(strings.eligibility)}: ${esc(it)}</p>") }
        append("<p class=\"note\">${esc(strings.availabilityUnknown)}</p>")
        append("<p class=\"small\">")
        r.sourceUrl?.let { append(esc(strings.sourceLink(it))).append("<br>") }
        strings.formatDate(r.verifiedDate)?.let { append(esc(strings.checkedOn(it))) }
        append("</p>")
        append("<p class=\"small\">${esc(strings.emergencyBody)} ${esc(strings.printHelpLine)}</p>")
        append("</article>")
    }
}
