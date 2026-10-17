package org.bridge.app.ui.screens

import androidx.compose.runtime.Composable
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ChoiceButton
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton
import org.bridge.app.ui.state.PhoneAccess

/** Manual city pick from the cities in the records. No GPS, no typing required. */
@Composable
fun CityScreen(strings: Strings, cities: List<String>, selected: String?, onChoose: (String?) -> Unit) {
    ScreenTitle(strings.cityTitle)
    Body(strings.cityHint, muted = true)
    cities.forEach { city -> ChoiceButton(city, city == selected) { onChoose(city) } }
    SecondaryButton(strings.cityNotSure, { onChoose(null) })
}

/** Phone access changes what a usable next step is (scenarios C06, C08). Skippable. */
@Composable
fun PhoneAccessScreen(strings: Strings, selected: PhoneAccess?, onChoose: (PhoneAccess?) -> Unit) {
    ScreenTitle(strings.phoneTitle)
    Body(strings.phoneHint, muted = true)
    ChoiceButton(strings.phoneYes, selected == PhoneAccess.YES) { onChoose(PhoneAccess.YES) }
    ChoiceButton(strings.phoneHard, selected == PhoneAccess.HARD) { onChoose(PhoneAccess.HARD) }
    ChoiceButton(strings.phoneNone, selected == PhoneAccess.NONE) { onChoose(PhoneAccess.NONE) }
    SecondaryButton(strings.skip, { onChoose(null) })
}
