package org.bridge.app.ui

import org.bridge.app.api.pick
import org.bridge.app.domain.Match
import org.bridge.app.domain.Plan
import org.bridge.app.i18n.Strings

/** One-page printable card as plain HTML: resource, phone, address, what to ask, source and date. */
fun printCardHtml(match: Match, plan: Plan, strings: Strings, language: String): String {
    val r = match.resource
    fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
    return buildString {
        append("<article class=\"card\" lang=\"$language\">")
        append("<p class=\"brand\">Bridge</p>")
        append("<h1>${esc(r.name)}</h1>")
        r.phone?.let { append("<p class=\"big\">☎ ${esc(it)}</p>") }
        r.address?.let { append("<p>${esc(strings.address)}: ${esc(it)}</p>") }
        r.website?.let { append("<p>${esc(it)}</p>") }
        append("<h2>${esc(plan.title ?: strings.nextStepTitle)}</h2><ol>")
        plan.steps.forEach { append("<li>${esc(it)}</li>") }
        append("</ol>")
        if (plan.questionsToAsk.isNotEmpty()) {
            append("<h2>${esc(strings.whatToAsk)}</h2><ul>")
            plan.questionsToAsk.forEach { append("<li>☐ ${esc(it)}</li>") }
            append("</ul>")
        }
        r.eligibilityNotes?.pick(language)?.let { append("<p>${esc(strings.eligibility)}: ${esc(it)}</p>") }
        append("<p class=\"note\">${esc(strings.availabilityUnknown)}</p>")
        append("<p class=\"small\">")
        r.sourceUrl?.let { append(esc(strings.reasonSource(it))).append("<br>") }
        r.lastVerified?.let { append(esc(strings.lastChecked(it))) }
        append("</p>")
        append("<p class=\"small\">${esc(strings.emergencyBody)} ${esc(strings.printHelpLine)}</p>")
        append("</article>")
    }
}
