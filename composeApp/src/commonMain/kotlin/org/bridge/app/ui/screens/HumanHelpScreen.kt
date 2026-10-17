package org.bridge.app.ui.screens

import androidx.compose.runtime.Composable
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.components.Body
import org.bridge.app.ui.components.ExternalActionButton
import org.bridge.app.ui.components.ExternalTarget
import org.bridge.app.ui.components.Notice
import org.bridge.app.ui.components.PrimaryButton
import org.bridge.app.ui.components.ScreenTitle
import org.bridge.app.ui.components.SecondaryButton
import org.bridge.app.ui.components.SectionTitle
import org.bridge.app.ui.theme.BridgeColors

// Static human-support routes. No AI, no detection logic, works offline. Bridge never contacts anyone itself.

@Composable
fun HumanHelpScreen(strings: Strings, onKeepLooking: () -> Unit) {
    EmergencyBlock(strings)
    ScreenTitle(strings.staffTitle)
    Body(strings.staffBody)
    ExternalActionButton(strings.call211, ExternalTarget("tel:211", strings.confirmCall("2-1-1")), strings, primary = true)
    SecondaryButton(strings.keepLooking, onKeepLooking)
}

/** Intake route "danger" (scenario C10): hotline and staff first; addresses stay hidden from here on. */
@Composable
fun DangerScreen(strings: Strings, onKeepLooking: () -> Unit) {
    ScreenTitle(strings.dangerTitle)
    Body(strings.dangerBody)
    ExternalActionButton(strings.call911, ExternalTarget("tel:911", strings.confirmCall("9-1-1")), strings, primary = true)
    ExternalActionButton(strings.call211, ExternalTarget("tel:211", strings.confirmCall("2-1-1")), strings)
    ExternalActionButton(strings.call988, ExternalTarget("tel:988", strings.confirmCall("9-8-8")), strings)
    SecondaryButton(strings.keepLooking, onKeepLooking)
}

/** Intake route "clinical" (scenario C11): no advice of any kind, only the route to a person. */
@Composable
fun ClinicalScreen(strings: Strings, onLookForSomethingElse: () -> Unit) {
    ScreenTitle(strings.clinicalTitle)
    Body(strings.clinicalBody)
    PrimaryButton(strings.clinicalBack, onLookForSomethingElse)
}

@Composable
private fun EmergencyBlock(strings: Strings) {
    SectionTitle(strings.emergencyTitle)
    Notice(strings.emergencyBody, BridgeColors.Danger, BridgeColors.DangerSoft)
    ExternalActionButton(strings.call911, ExternalTarget("tel:911", strings.confirmCall("9-1-1")), strings)
    ExternalActionButton(strings.call988, ExternalTarget("tel:988", strings.confirmCall("9-8-8")), strings)
}
