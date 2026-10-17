package org.bridge.app.i18n

import org.bridge.app.domain.AccessKind

enum class Language(val code: String, val label: String) {
    EN("en", "English"),
    ES("es", "Español"),
}

data class Strings(
    val language: Language,
    val welcomeTitle: String,
    val welcomeBody: String,
    val welcomeDisclaimer: String,
    val chooseLanguage: String,
    val start: String,
    val back: String,
    val skip: String,
    val startOver: String,
    val continueLabel: String,
    val needHelpNow: String,
    val workingOffline: String,
    val needTitle: String,
    val needHint: String,
    val somethingElse: String,
    val moreNeedsTitle: String,
    val describeLabel: String,
    val describeHint: String,
    val describeSubmit: String,
    val interpreting: String,
    val intakeFallback: String,
    val cityTitle: String,
    val cityHint: String,
    val cityNotSure: String,
    val phoneTitle: String,
    val phoneHint: String,
    val phoneYes: String,
    val phoneHard: String,
    val phoneNone: String,
    val followUpOptional: String,
    val loadingResults: String,
    val loadError: String,
    val tryAgain: String,
    val resultsTitle: String,
    val resultsNone: String,
    val resultsNoneBody: String,
    val howToGetHelp: String,
    val accessExplain: (AccessKind) -> String,
    val referralUnknown: String,
    val whyFit: String,
    val whyAmISeeing: String,
    val hideWhy: String,
    val reasonOffers: (String) -> String,
    val reasonInYourCity: (String) -> String,
    val reasonInOtherCity: (String) -> String,
    val reasonNotTiedToCity: String,
    val reasonNoPhoneNeeded: String,
    val sourceSays: String,
    val sourceLink: (String) -> String,
    val checkedOn: (String) -> String,
    val confidenceMedium: String,
    val confidenceLow: String,
    val notesTitle: String,
    val availabilityUnknown: String,
    val eligibility: String,
    val cost: String,
    val hours: String,
    val languages: String,
    val sourceInEnglish: String,
    val phoneLabel: String,
    val call: (String) -> String,
    val openWebsite: (String) -> String,
    val address: String,
    val addressOffice: String,
    val addressWithheld: String,
    val addressNeedsReferral: String,
    val chooseThis: String,
    val nextStepTitle: String,
    val stepsFor: (AccessKind, Boolean, Boolean) -> List<String>,
    val questionsFor: (AccessKind) -> List<String>,
    val whatToAsk: String,
    /** "Before you go" - see Preparation. */
    val beforeYouGo: String,
    val doesThisFit: String,
    val whatToBring: String,
    val headsUpTitle: String,
    val printCard: String,
    val printHelpLine: String,
    val didntWork: String,
    val askStaff: String,
    val staffTitle: String,
    val staffBody: String,
    val call211: String,
    val emergencyTitle: String,
    val emergencyBody: String,
    val call911: String,
    val call988: String,
    val keepLooking: String,
    val dangerTitle: String,
    val dangerBody: String,
    val clinicalTitle: String,
    val clinicalBody: String,
    val clinicalBack: String,
    val confirmLeaveTitle: String,
    val confirmCall: (String) -> String,
    val confirmOpen: (String) -> String,
    val confirm: String,
    val cancel: String,
    val months: List<String>,
    val pageNeedsInternet: String,
    val pageNeedsInternetBody: String,
    val pageFailed: String,
)

private val CommonQuestionsEn = listOf("Can you help someone in my situation?", "What should I bring?", "What are your hours?")
private val CommonQuestionsEs = listOf("¿Pueden ayudar a alguien en mi situación?", "¿Qué debo traer?", "¿Cuál es su horario?")

val English = Strings(
    language = Language.EN,
    welcomeTitle = "Find your next step",
    welcomeBody = "Bridge helps you find places in Orange County that might help, and one thing you can do today. No account needed.",
    welcomeDisclaimer = "Bridge is not an emergency service. It can't promise a place has space or that you qualify. Always check with the provider.",
    chooseLanguage = "Language",
    start = "Start",
    back = "Back",
    skip = "Skip",
    startOver = "Start over",
    continueLabel = "Continue",
    needHelpNow = "Need help now?",
    workingOffline = "Working offline. Showing information saved on this device.",
    needTitle = "What would help most right now?",
    needHint = "Pick one. You can change it later.",
    somethingElse = "Something else",
    moreNeedsTitle = "What do you need help with?",
    describeLabel = "Or say it in your own words (optional)",
    describeHint = "For example: no car, need somewhere tonight in Anaheim",
    describeSubmit = "Find options",
    interpreting = "Reading what you wrote…",
    intakeFallback = "Please pick the closest option above.",
    cityTitle = "What city are you in or near?",
    cityHint = "This helps show places closer to you.",
    cityNotSure = "Not sure / skip",
    phoneTitle = "Can you make phone calls?",
    phoneHint = "This helps us show places you can actually reach.",
    phoneYes = "Yes",
    phoneHard = "Calls are hard for me",
    phoneNone = "I don't have a phone",
    followUpOptional = "Optional. You can skip this.",
    loadingResults = "Finding possible options…",
    loadError = "We couldn't load results. You can try again or ask for help.",
    tryAgain = "Try again",
    resultsTitle = "Places that might help",
    resultsNone = "We didn't find a match",
    resultsNoneBody = "We only show places that have been checked, so we won't guess. IHH staff or 2-1-1 may know more options.",
    howToGetHelp = "How to get help here",
    accessExplain = {
        when (it) {
            AccessKind.REFERRAL -> "You need a referral. Going there without one may not work."
            AccessKind.APPOINTMENT -> "By appointment."
            AccessKind.PHONE -> "By phone."
            AccessKind.WALK_IN -> "You can go in person."
            AccessKind.ONLINE -> "Online, on their website."
            AccessKind.MIXED -> "More than one way: see the options below."
            AccessKind.UNKNOWN -> "We don't know. Ask when you contact them."
        }
    },
    referralUnknown = "We don't know if you need a referral. Ask when you contact them.",
    whyFit = "Why this might fit",
    whyAmISeeing = "Why am I seeing this?",
    hideWhy = "Hide details",
    reasonOffers = { "It offers help with: $it" },
    reasonInYourCity = { "It's in $it, the city you picked" },
    reasonInOtherCity = { "It's in $it" },
    reasonNotTiedToCity = "It isn't tied to one city",
    reasonNoPhoneNeeded = "You can reach it without calling",
    sourceSays = "What the source says, word for word:",
    sourceLink = { "Source: $it" },
    checkedOn = { "Checked $it" },
    confidenceMedium = "Some details could not be fully checked. Confirm before you rely on them.",
    confidenceLow = "We could not confirm this listing. It may be out of date. Confirm before you go.",
    notesTitle = "Notes from the people who checked this",
    availabilityUnknown = "Availability not confirmed. Contact the provider.",
    eligibility = "Who it's for",
    cost = "Cost",
    hours = "Hours",
    languages = "Languages",
    sourceInEnglish = "",
    phoneLabel = "Phone",
    call = { "Call $it" },
    openWebsite = { "Open website ($it)" },
    address = "Address",
    addressOffice = "Address on file. It may be an office, not where services happen.",
    addressWithheld = "Address not shown, for safety. Contact them to ask where to go.",
    addressNeedsReferral = "Don't go to this address without a referral. You may be turned away.",
    chooseThis = "See next step",
    nextStepTitle = "Your next step",
    stepsFor = { kind, hasPhone, cannotCall ->
        when (kind) {
            AccessKind.REFERRAL -> listOfNotNull(
                "This place only takes people who have a referral.",
                "Ask an IHH staff member to help you get a referral.",
                "You can also call and ask how referrals work.".takeIf { hasPhone && !cannotCall },
            )
            AccessKind.APPOINTMENT -> listOf("They see people by appointment. Call or use their website to book one.")
            AccessKind.PHONE ->
                if (cannotCall) listOf("This one is mostly by phone. Ask an IHH staff member to call with you.")
                else listOf("Call and ask if they can help you.")
            AccessKind.WALK_IN -> listOf("You can go in person. Check the hours before you go.")
            AccessKind.ONLINE -> listOf("Start on their website.", "No internet? Ask IHH staff to help you look.")
            AccessKind.MIXED -> listOf("There is more than one way to reach them. Pick the one that works for you.")
            AccessKind.UNKNOWN -> listOf("Contact them to check they can help you, and how.")
        }
    },
    questionsFor = {
        if (it == AccessKind.REFERRAL) listOf("How do I get a referral to you?", "Is there a waitlist?") + CommonQuestionsEn
        else CommonQuestionsEn
    },
    whatToAsk = "What to ask",
    beforeYouGo = "Before you go",
    doesThisFit = "Does this sound like you?",
    whatToBring = "What to bring",
    headsUpTitle = "Worth knowing",
    printCard = "Print this card",
    printHelpLine = "For other services in Orange County, call 2-1-1.",
    didntWork = "This didn't work: show other options",
    askStaff = "Ask IHH staff for help",
    staffTitle = "Help from a person",
    staffBody = "IHH staff can help you with any of this. You can show them this screen. You can also call 2-1-1 any time for free help finding services in Orange County.",
    call211 = "Call 2-1-1",
    emergencyTitle = "If you're in danger right now",
    emergencyBody = "Bridge can't send help. If you or someone else is in immediate danger, call 9-1-1. For a mental health crisis, call or text 9-8-8.",
    call911 = "Call 9-1-1",
    call988 = "Call or text 9-8-8",
    keepLooking = "Keep looking for places",
    dangerTitle = "Your safety comes first",
    dangerBody = "Please tell an IHH staff member now so they can help you stay safe. If you are in danger right now, call 9-1-1. If you share this device, you can tap Start over when you're done.",
    clinicalTitle = "That's a question for your care team",
    clinicalBody = "Bridge can't answer questions about medicine or health. Please ask the nurse or an IHH staff member. They can help you, or connect you with a doctor.",
    clinicalBack = "Look for something else",
    confirmLeaveTitle = "Continue?",
    confirmCall = { "This will start a phone call to $it." },
    confirmOpen = { "This will open $it." },
    confirm = "Yes, continue",
    cancel = "Cancel",
    pageNeedsInternet = "Internet connection required",
    pageNeedsInternetBody = "This page is on another website, so it needs the internet. Everything else in Bridge works without it. You can try again when you're connected, or write down the address below.",
    pageFailed = "This page didn't load. The website may be down or your connection may be weak. You can try again, or write down the address below.",
    months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
)

val Spanish = Strings(
    language = Language.ES,
    welcomeTitle = "Encuentra tu próximo paso",
    welcomeBody = "Bridge te ayuda a encontrar lugares en el Condado de Orange que podrían ayudarte, y una cosa que puedes hacer hoy. No necesitas cuenta.",
    welcomeDisclaimer = "Bridge no es un servicio de emergencia. No puede prometer que un lugar tenga espacio o que califiques. Siempre confirma con el proveedor.",
    chooseLanguage = "Idioma",
    start = "Empezar",
    back = "Atrás",
    skip = "Omitir",
    startOver = "Empezar de nuevo",
    continueLabel = "Continuar",
    needHelpNow = "¿Necesitas ayuda ya?",
    workingOffline = "Sin conexión. Mostrando información guardada en este dispositivo.",
    needTitle = "¿Qué te ayudaría más ahora?",
    needHint = "Elige una opción. Puedes cambiarla después.",
    somethingElse = "Otra cosa",
    moreNeedsTitle = "¿Con qué necesitas ayuda?",
    describeLabel = "O dilo con tus palabras (opcional)",
    describeHint = "Por ejemplo: no tengo carro, necesito dónde dormir esta noche en Anaheim",
    describeSubmit = "Buscar opciones",
    interpreting = "Leyendo lo que escribiste…",
    intakeFallback = "Por favor elige la opción más cercana arriba.",
    cityTitle = "¿En qué ciudad estás o cerca de cuál?",
    cityHint = "Esto ayuda a mostrar lugares más cercanos.",
    cityNotSure = "No sé / omitir",
    phoneTitle = "¿Puedes hacer llamadas?",
    phoneHint = "Así te mostramos lugares a los que de verdad puedes llegar.",
    phoneYes = "Sí",
    phoneHard = "Me cuesta hablar por teléfono",
    phoneNone = "No tengo teléfono",
    followUpOptional = "Opcional. Puedes omitir esto.",
    loadingResults = "Buscando opciones posibles…",
    loadError = "No pudimos cargar resultados. Puedes intentar de nuevo o pedir ayuda.",
    tryAgain = "Intentar de nuevo",
    resultsTitle = "Lugares que podrían ayudar",
    resultsNone = "No encontramos una opción",
    resultsNoneBody = "Solo mostramos lugares que han sido revisados, así que no vamos a adivinar. El personal de IHH o el 2-1-1 pueden conocer más opciones.",
    howToGetHelp = "Cómo recibir ayuda aquí",
    accessExplain = {
        when (it) {
            AccessKind.REFERRAL -> "Necesitas una referencia. Ir sin una puede no funcionar."
            AccessKind.APPOINTMENT -> "Con cita."
            AccessKind.PHONE -> "Por teléfono."
            AccessKind.WALK_IN -> "Puedes ir en persona."
            AccessKind.ONLINE -> "En línea, en su sitio web."
            AccessKind.MIXED -> "De varias formas: mira las opciones abajo."
            AccessKind.UNKNOWN -> "No lo sabemos. Pregunta cuando te comuniques."
        }
    },
    referralUnknown = "No sabemos si necesitas una referencia. Pregunta cuando te comuniques.",
    whyFit = "Por qué podría servirte",
    whyAmISeeing = "¿Por qué veo esto?",
    hideWhy = "Ocultar detalles",
    reasonOffers = { "Ofrece ayuda con: $it" },
    reasonInYourCity = { "Está en $it, la ciudad que elegiste" },
    reasonInOtherCity = { "Está en $it" },
    reasonNotTiedToCity = "No depende de una ciudad",
    reasonNoPhoneNeeded = "Puedes comunicarte sin llamar",
    sourceSays = "Lo que dice la fuente, palabra por palabra (en inglés):",
    sourceLink = { "Fuente: $it" },
    checkedOn = { "Revisado el $it" },
    confidenceMedium = "Algunos detalles no se pudieron confirmar del todo. Verifícalos antes de contar con ellos.",
    confidenceLow = "No pudimos confirmar este lugar. Puede estar desactualizado. Confirma antes de ir.",
    notesTitle = "Notas de quienes revisaron esto (en inglés)",
    availabilityUnknown = "Disponibilidad no confirmada. Comunícate con el proveedor.",
    eligibility = "Para quién es",
    cost = "Costo",
    hours = "Horario",
    languages = "Idiomas",
    sourceInEnglish = "La información del proveedor está en inglés, tal como aparece en la fuente.",
    phoneLabel = "Teléfono",
    call = { "Llamar al $it" },
    openWebsite = { "Abrir sitio web ($it)" },
    address = "Dirección",
    addressOffice = "Dirección registrada. Puede ser una oficina, no donde se dan los servicios.",
    addressWithheld = "Dirección oculta por seguridad. Comunícate para preguntar a dónde ir.",
    addressNeedsReferral = "No vayas a esta dirección sin una referencia. Es posible que no te dejen entrar.",
    chooseThis = "Ver próximo paso",
    nextStepTitle = "Tu próximo paso",
    stepsFor = { kind, hasPhone, cannotCall ->
        when (kind) {
            AccessKind.REFERRAL -> listOfNotNull(
                "Este lugar solo recibe a personas con una referencia.",
                "Pide a alguien del personal de IHH que te ayude a conseguir una referencia.",
                "También puedes llamar y preguntar cómo funcionan las referencias.".takeIf { hasPhone && !cannotCall },
            )
            AccessKind.APPOINTMENT -> listOf("Atienden con cita. Llama o usa su sitio web para pedir una.")
            AccessKind.PHONE ->
                if (cannotCall) listOf("Este lugar atiende sobre todo por teléfono. Pide a alguien del personal de IHH que llame contigo.")
                else listOf("Llama y pregunta si te pueden ayudar.")
            AccessKind.WALK_IN -> listOf("Puedes ir en persona. Revisa el horario antes de ir.")
            AccessKind.ONLINE -> listOf("Empieza en su sitio web.", "¿No tienes internet? Pide ayuda al personal de IHH.")
            AccessKind.MIXED -> listOf("Hay varias formas de comunicarte. Elige la que te funcione.")
            AccessKind.UNKNOWN -> listOf("Comunícate para confirmar que te pueden ayudar, y cómo.")
        }
    },
    questionsFor = {
        if (it == AccessKind.REFERRAL) listOf("¿Cómo consigo una referencia para ustedes?", "¿Hay lista de espera?") + CommonQuestionsEs
        else CommonQuestionsEs
    },
    whatToAsk = "Qué preguntar",
    beforeYouGo = "Antes de ir",
    doesThisFit = "¿Esto se parece a tu situación?",
    whatToBring = "Qué llevar",
    headsUpTitle = "Bueno saber",
    printCard = "Imprimir esta tarjeta",
    printHelpLine = "Para otros servicios en el Condado de Orange, llama al 2-1-1.",
    didntWork = "Esto no funcionó: ver otras opciones",
    askStaff = "Pedir ayuda al personal de IHH",
    staffTitle = "Ayuda de una persona",
    staffBody = "El personal de IHH puede ayudarte con todo esto. Puedes mostrarles esta pantalla. También puedes llamar al 2-1-1 en cualquier momento para recibir ayuda gratis para encontrar servicios en el Condado de Orange.",
    call211 = "Llamar al 2-1-1",
    emergencyTitle = "Si estás en peligro ahora",
    emergencyBody = "Bridge no puede enviar ayuda. Si tú u otra persona están en peligro inmediato, llama al 9-1-1. Para una crisis de salud mental, llama o envía un mensaje al 9-8-8.",
    call911 = "Llamar al 9-1-1",
    call988 = "Llamar o escribir al 9-8-8",
    keepLooking = "Seguir buscando lugares",
    dangerTitle = "Tu seguridad es lo primero",
    dangerBody = "Por favor avisa ahora a alguien del personal de IHH para que te ayude a estar a salvo. Si estás en peligro ahora, llama al 9-1-1. Si compartes este dispositivo, puedes tocar Empezar de nuevo al terminar.",
    clinicalTitle = "Esa pregunta es para tu equipo de cuidado",
    clinicalBody = "Bridge no puede responder preguntas sobre medicinas o salud. Por favor pregunta a la enfermera o a alguien del personal de IHH. Te pueden ayudar o conectar con un médico.",
    clinicalBack = "Buscar otra cosa",
    confirmLeaveTitle = "¿Continuar?",
    confirmCall = { "Esto iniciará una llamada a $it." },
    confirmOpen = { "Esto abrirá $it." },
    confirm = "Sí, continuar",
    cancel = "Cancelar",
    pageNeedsInternet = "Se necesita conexión a internet",
    pageNeedsInternetBody = "Esta página está en otro sitio web, así que necesita internet. Todo lo demás en Bridge funciona sin conexión. Puedes intentar de nuevo cuando tengas conexión, o anotar la dirección de abajo.",
    pageFailed = "Esta página no cargó. Puede que el sitio esté caído o que tu conexión sea débil. Puedes intentar de nuevo, o anotar la dirección de abajo.",
    months = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"),
)

fun stringsFor(language: Language): Strings = when (language) {
    Language.EN -> English
    Language.ES -> Spanish
}

/** "2026-10-13" -> "13 Oct 2026" / "13 oct 2026". Falls back to the raw text. */
fun Strings.formatDate(iso: String?): String? {
    if (iso == null) return null
    val p = iso.take(10).split('-').mapNotNull { it.toIntOrNull() }
    if (p.size != 3 || p[1] !in 1..12) return iso
    return "${p[2]} ${months[p[1] - 1]} ${p[0]}"
}
