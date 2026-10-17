package org.bridge.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.bridge.app.i18n.Language
import org.bridge.app.i18n.Strings
import org.bridge.app.ui.theme.BridgeColors

/**
 * Shared page frame: top bar (Back / language / Start over), optional mock banner,
 * and a centered, scrollable column capped at phone-friendly width.
 */
@Composable
fun BridgeScaffold(
    strings: Strings,
    language: Language,
    onLanguageChange: (Language) -> Unit,
    showMockBanner: Boolean,
    showOffline: Boolean,
    onNeedHelp: (() -> Unit)?,
    onBack: (() -> Unit)?,
    onStartOver: (() -> Unit)?,
    /** Changes whenever the screen changes, so each screen starts scrolled to the top. */
    screenKey: Any,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(BridgeColors.Background)) {
        if (showMockBanner) {
            Text(
                strings.mockBanner,
                color = BridgeColors.OnPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().background(BridgeColors.Danger).padding(6.dp),
            )
        }
        Row(
            Modifier.fillMaxWidth().background(BridgeColors.Surface).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) NavTextButton(strings.back, onBack)
            Spacer(Modifier.weight(1f))
            val other = if (language == Language.EN) Language.ES else Language.EN
            NavTextButton(other.label, { onLanguageChange(other) })
            if (onStartOver != null) NavTextButton(strings.startOver, onStartOver)
        }
        // Crisis route: static, no AI, works offline, reachable from every screen.
        if (onNeedHelp != null) {
            Row(Modifier.fillMaxWidth().background(BridgeColors.DangerSoft).padding(horizontal = 4.dp)) {
                NavTextButton(strings.needHelpNow, onNeedHelp, color = BridgeColors.Danger)
            }
        }
        HorizontalDivider(color = BridgeColors.Border.copy(alpha = 0.4f))
        if (showOffline) {
            Text(
                strings.workingOffline,
                color = BridgeColors.InkMuted,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().background(BridgeColors.PrimarySoft)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        Box(
            Modifier.fillMaxSize().verticalScroll(remember(screenKey) { ScrollState(0) }),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                Modifier.widthIn(max = 560.dp).fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content,
            )
        }
    }
}

@Composable
fun ScreenTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineMedium,
        color = BridgeColors.Ink,
        modifier = Modifier.semantics { heading() },
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = BridgeColors.Ink,
        modifier = Modifier.semantics { heading() },
    )
}

@Composable
fun Body(text: String, muted: Boolean = false) {
    Text(
        text,
        style = MaterialTheme.typography.bodyLarge,
        color = if (muted) BridgeColors.InkMuted else BridgeColors.Ink,
    )
}
