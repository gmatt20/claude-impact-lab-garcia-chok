package org.bridge.app.i18n

/** The 16 seed categories. Codes are the wire values. */
enum class Need(val code: String) {
    SHELTER("shelter"),
    EMPLOYMENT("employment"),
    MICROENTERPRISE("microenterprise"),
    FAMILY_TRAVEL("family-reunification-travel"),
    HOUSING_NAVIGATION("housing-navigation"),
    ID_DOCUMENTS("id-documents"),
    TRANSPORTATION("transportation"),
    PHONE_INTERNET("phone-internet"),
    BENEFITS("benefits"),
    FOOD("food"),
    HEALTH_COVERAGE("health-coverage"),
    BEHAVIORAL_HEALTH("behavioral-health"),
    LEGAL("legal"),
    VETERANS("veterans"),
    HYGIENE_MAIL_STORAGE("hygiene-mail-storage"),
    OTHER("other"),
    ;

    companion object {
        /** IHH's four named needs lead the screen. */
        val headline = listOf(SHELTER, EMPLOYMENT, MICROENTERPRISE, FAMILY_TRAVEL)
        val more = entries - headline.toSet()
        fun fromCode(code: String?) = entries.firstOrNull { it.code == code }
    }
}

fun needLabel(need: Need, language: Language): String = when (language) {
    Language.EN -> when (need) {
        Need.SHELTER -> "A safe place to sleep"
        Need.EMPLOYMENT -> "Help finding work"
        Need.MICROENTERPRISE -> "A small loan to get working"
        Need.FAMILY_TRAVEL -> "Getting home to family"
        Need.HOUSING_NAVIGATION -> "Help finding housing"
        Need.ID_DOCUMENTS -> "ID and documents"
        Need.TRANSPORTATION -> "Getting around (bus, rides)"
        Need.PHONE_INTERNET -> "A phone or internet"
        Need.BENEFITS -> "Money and benefits (CalFresh, Medi-Cal)"
        Need.FOOD -> "Food"
        Need.HEALTH_COVERAGE -> "Health insurance"
        Need.BEHAVIORAL_HEALTH -> "Mental health or substance use support"
        Need.LEGAL -> "Legal help"
        Need.VETERANS -> "Help for veterans"
        Need.HYGIENE_MAIL_STORAGE -> "Showers, laundry or mail"
        Need.OTHER -> "Not sure: a general help line"
    }
    Language.ES -> when (need) {
        Need.SHELTER -> "Un lugar seguro para dormir"
        Need.EMPLOYMENT -> "Ayuda para encontrar trabajo"
        Need.MICROENTERPRISE -> "Un préstamo pequeño para empezar a trabajar"
        Need.FAMILY_TRAVEL -> "Volver con mi familia"
        Need.HOUSING_NAVIGATION -> "Ayuda para encontrar vivienda"
        Need.ID_DOCUMENTS -> "Identificación y documentos"
        Need.TRANSPORTATION -> "Transporte (autobús, viajes)"
        Need.PHONE_INTERNET -> "Teléfono o internet"
        Need.BENEFITS -> "Dinero y beneficios (CalFresh, Medi-Cal)"
        Need.FOOD -> "Comida"
        Need.HEALTH_COVERAGE -> "Seguro médico"
        Need.BEHAVIORAL_HEALTH -> "Apoyo de salud mental o uso de sustancias"
        Need.LEGAL -> "Ayuda legal"
        Need.VETERANS -> "Ayuda para veteranos"
        Need.HYGIENE_MAIL_STORAGE -> "Duchas, lavandería o correo"
        Need.OTHER -> "No sé: una línea de ayuda general"
    }
}

fun needLabel(code: String, language: Language): String =
    Need.fromCode(code)?.let { needLabel(it, language) } ?: code
