package org.bridge.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.bridge.app.ui.theme.BridgeColors

@Composable
fun Notice(text: String, color: Color = BridgeColors.Caution, background: Color = BridgeColors.CautionSoft) {
    Text(
        text,
        color = color,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth()
            .background(background, RoundedCornerShape(10.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(12.dp),
    )
}

@Composable
fun LoadingRow(text: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 24.dp).semantics { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(color = BridgeColors.Primary)
        Body(text)
    }
}

@Composable
fun ErrorBlock(message: String, retryLabel: String, onRetry: () -> Unit, helpLabel: String, onHelp: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Notice(message, BridgeColors.Danger, BridgeColors.DangerSoft)
        PrimaryButton(retryLabel, onRetry)
        SecondaryButton(helpLabel, onHelp)
    }
}
