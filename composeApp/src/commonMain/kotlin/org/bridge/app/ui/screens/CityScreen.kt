package org.bridge.app.ui.screens

import androidx.compose.runtime.Composable
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ChoiceButton
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton

/** Manual city pick from the cities the catalog covers. No GPS, no typing required. */
@Composable
fun CityScreen(strings: Strings, cities: List<String>, selected: String?, onChoose: (String?) -> Unit) {
    ScreenTitle(strings.cityTitle)
    Body(strings.cityHint, muted = true)
    cities.forEach { city -> ChoiceButton(city, city == selected) { onChoose(city) } }
    SecondaryButton(strings.cityNotSure, { onChoose(null) })
}
