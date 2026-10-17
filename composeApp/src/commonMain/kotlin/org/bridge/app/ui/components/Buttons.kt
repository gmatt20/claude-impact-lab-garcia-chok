package org.bridge.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.bridge.app.ui.theme.BridgeColors

private val Shape = RoundedCornerShape(12.dp)
private val Padding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)

/** A visible focus ring for keyboard users, layered over whatever border the button has. */
@Composable
private fun focusBorder(source: MutableInteractionSource, fallback: BorderStroke?): BorderStroke? {
    val focused by source.collectIsFocusedAsState()
    return if (focused) BorderStroke(3.dp, BridgeColors.Focus) else fallback
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val source = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = Shape,
        contentPadding = Padding,
        interactionSource = source,
        border = focusBorder(source, null),
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
    ) { Text(text, textAlign = TextAlign.Center) }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = BridgeColors.Primary,
) {
    val source = remember { MutableInteractionSource() }
    OutlinedButton(
        onClick = onClick,
        shape = Shape,
        contentPadding = Padding,
        interactionSource = source,
        border = focusBorder(source, BorderStroke(2.dp, color)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
    ) { Text(text, textAlign = TextAlign.Center) }
}

/** Large tappable choice, used for goals. */
@Composable
fun ChoiceButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    OutlinedButton(
        onClick = onClick,
        shape = Shape,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        interactionSource = source,
        border = focusBorder(
            source,
            BorderStroke(if (selected) 3.dp else 1.5.dp, if (selected) BridgeColors.Primary else BridgeColors.Border),
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) BridgeColors.PrimarySoft else BridgeColors.Surface,
            contentColor = BridgeColors.Ink,
        ),
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
    ) { Text(text, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start) }
}

@Composable
fun NavTextButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = BridgeColors.Primary) {
    val source = remember { MutableInteractionSource() }
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = color),
        interactionSource = source,
        border = focusBorder(source, null),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        modifier = modifier.heightIn(min = 48.dp),
    ) { Text(text) }
}
