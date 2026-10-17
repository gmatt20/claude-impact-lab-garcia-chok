package org.bridge.app.api

// Only build actions for contact values that look valid; never invent or "fix" contacts.

fun telUri(phone: String?): String? {
    if (phone.isNullOrBlank()) return null
    val digits = phone.filter { it.isDigit() || it == '+' }
    val count = digits.count { it.isDigit() }
    if (count !in 3..15 || digits.all { it == '0' || it == '+' }) return null
    return "tel:$digits"
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
