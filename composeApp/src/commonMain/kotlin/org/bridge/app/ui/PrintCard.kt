package org.bridge.app.ui

import org.bridge.app.api.Preparation
import org.bridge.app.api.SeedResource
import org.bridge.app.domain.AccessKind
import org.bridge.app.domain.accessKind
import org.bridge.app.domain.addressIsDestination
import org.bridge.app.domain.isAddressWithheld
import org.bridge.app.i18n.Strings
import org.bridge.app.i18n.formatDate

/** One-page printable card as plain HTML: name, how to access, phone, what to ask, source and checked date. */
fun printCardHtml(
    r: SeedResource,
    steps: List<String>,
    questions: List<String>,
    strings: Strings,
    safetyMode: Boolean,
    preparation: Preparation? = null,
): String {
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
        // "Before you go": approved copy only, so the printed page carries no unreviewed text.
        preparation?.let { prep ->
            if (prep.fits.isNotEmpty()) {
                append("<h2>${esc(strings.doesThisFit)}</h2><ul>")
                prep.fits.forEach { append("<li>☐ ${esc(it)}</li>") }
                append("</ul>")
            }
            if (prep.bring.isNotEmpty()) {
                append("<h2>${esc(strings.whatToBring)}</h2><ul>")
                prep.bring.forEach { append("<li>☐ ${esc(it)}</li>") }
                append("</ul>")
            }
            prep.headsUp?.takeIf { it.isNotBlank() }?.let {
                append("<p class=\"note\">${esc(strings.headsUpTitle)}: ${esc(it)}</p>")
            }
        }
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

/** Styles for the printed card. The web build has the same rules in index.html's print stylesheet. */
private const val PRINT_CSS = """
body { font: 14pt/1.45 Georgia, "Times New Roman", serif; color: #000; margin: 0; }
.card { max-width: 7in; margin: 0 auto; }
.brand { font: 600 11pt -apple-system, system-ui, sans-serif; letter-spacing: .08em; text-transform: uppercase; margin: 0 0 4pt; }
h1 { font-size: 22pt; margin: 0 0 8pt; }
h2 { font-size: 15pt; margin: 14pt 0 4pt; border-bottom: 1px solid #000; }
.big { font-size: 20pt; font-weight: 700; margin: 4pt 0; }
.note { border: 1.5px solid #000; padding: 6pt 8pt; }
.small { font-size: 10.5pt; }
li { margin: 3pt 0; }
"""

/** A standalone HTML document for native print APIs (Android PrintManager, iOS UIPrintInteractionController). */
fun printableDocument(cardHtml: String): String =
    "<!doctype html><html><head><meta charset=\"utf-8\"><style>$PRINT_CSS</style></head><body>$cardHtml</body></html>"
