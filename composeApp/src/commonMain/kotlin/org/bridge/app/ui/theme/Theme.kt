package org.bridge.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Calm, warm palette. All text/background pairs meet WCAG AA (most meet AAA).
object BridgeColors {
    val Background = Color(0xFFFBF8F3)
    val Surface = Color(0xFFFFFFFF)
    val Ink = Color(0xFF1C1B1A)
    val InkMuted = Color(0xFF4A4744)
    val Primary = Color(0xFF1F5C4E)       // deep teal, 7.9:1 on white
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimarySoft = Color(0xFFE3EFEB)
    val Border = Color(0xFF8A847D)
    val Focus = Color(0xFF0B57D0)
    val Caution = Color(0xFF7A4A00)       // amber-brown text, 7:1 on CautionSoft
    val CautionSoft = Color(0xFFFFF2DC)
    val Danger = Color(0xFF9B1C1C)
    val DangerSoft = Color(0xFFFDECEC)
}

private val typography = Typography(
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 19.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 18.sp, lineHeight = 27.sp),
    bodyMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    labelLarge = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
)

@Composable
fun BridgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BridgeColors.Primary,
            onPrimary = BridgeColors.OnPrimary,
            background = BridgeColors.Background,
            onBackground = BridgeColors.Ink,
            surface = BridgeColors.Surface,
            onSurface = BridgeColors.Ink,
            onSurfaceVariant = BridgeColors.InkMuted,
            outline = BridgeColors.Border,
            error = BridgeColors.Danger,
        ),
        typography = typography,
        content = content,
    )
}
