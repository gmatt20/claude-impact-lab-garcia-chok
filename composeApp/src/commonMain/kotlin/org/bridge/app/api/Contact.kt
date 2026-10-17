package org.bridge.app.api

// Only build actions from contact values literally present in a record; never invent or "fix" one.

/** A phone number found verbatim in a record's phone text. */
data class PhoneNumber(val display: String, val tel: String)

// North American numbers in the formats the seed uses: (714) 754-5346, 714-571-5200, 619.884.8467,
// 1-866-272-0349, (714)542-3577; plus the short codes 2-1-1 and 988.
private val PhonePattern = Regex("""(?:1[-.\s])?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]\d{4}|\b2-1-1\b|\b211\b|\b988\b""")

/**
 * Pulls dialable numbers out of free text like "2-1-1 or 888-600-4357 (toll-free)".
 * Text with letters in the number (1-800-RUNAWAY) only yields the digits version if it is also written out.
 */
fun phoneNumbers(text: String?): List<PhoneNumber> {
    if (text.isNullOrBlank()) return emptyList()
    return PhonePattern.findAll(text)
        .map { m ->
            val display = m.value.trim()
            val digits = display.filter { it.isDigit() }
            PhoneNumber(display, "tel:$digits")
        }
        .distinctBy { it.tel }
        .toList()
}

fun safeWebUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null
    val trimmed = url.trim()
    if (!(trimmed.startsWith("https://") || trimmed.startsWith("http://"))) return null
    if (trimmed.any { it.isWhitespace() }) return null
    return trimmed
}

fun displayHost(url: String): String =
    url.substringAfter("://").substringBefore('/').removePrefix("www.")
