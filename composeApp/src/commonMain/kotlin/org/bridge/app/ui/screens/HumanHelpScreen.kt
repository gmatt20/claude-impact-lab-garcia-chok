package org.bridge.app.ui.screens

import androidx.compose.runtime.Composable
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ExternalActionButton
import org.bridge.app.ui.components.ExternalTarget
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton
import org.bridge.app.ui.components.SectionTitle
import org.bridge.app.ui.theme.BridgeColors

/** Static human-support routes. No AI, no detection logic, works offline. Bridge never dispatches help itself. */
@Composable
fun HumanHelpScreen(strings: Strings, onKeepLooking: () -> Unit) {
    SectionTitle(strings.emergencyTitle)
    Notice(strings.emergencyBody, BridgeColors.Danger, BridgeColors.DangerSoft)
    ExternalActionButton(strings.call911, ExternalTarget("tel:911", strings.confirmCall("9-1-1")), strings)
    ExternalActionButton(strings.call988, ExternalTarget("tel:988", strings.confirmCall("9-8-8")), strings)
    ScreenTitle(strings.caseworkerTitle)
    Body(strings.caseworkerBody)
    ExternalActionButton(strings.call211, ExternalTarget("tel:211", strings.confirmCall("2-1-1")), strings, primary = true)
    SecondaryButton(strings.keepLooking, onKeepLooking)
}
