package com.examhacker.resources

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

class ColorPreset {
    companion object {

        val Black = Color(0xFF000000)
        val Transparent = Black.copy(alpha = 0f)

        val BackgroundVariant = Color(0xFFFFFFFF)
        val BackgroundDefaultPrimary = Color(0xFFF3F6F7)
        val BackgroundDefaultSecondary = Color(0xFFF5F5F5)
        val BackgroundFocus = Color(0xFFE7FFFF)
        val BackgroundPositiveSecondary = Color(0xFFCFF7D3)
        val BackgroundDangerSecondary = Color(0xFFFFBDBD)
        val BackgroundWarningSecondary = Color(0xFFFFF1C2)

        val BorderDefault = Color(0xFFD9D9D9)
        val BorderSecondary = Color(0xFFAAAAAA)
        val BorderFocus = Color(0xFF7EA1A9)
        val BorderPositiveTertiary = Color(0xFF14AE5C)
        val BorderDangerTertiary = Color(0xFFEC221F)
        val TextDefaultSecondary = Color(0xFF757575)

        val TextPositivePrimary = Color(0xFF02542D)
        val TextWarningTertiary = Color(0xFFBF6A02)
        val IconPositiveTertiary = Color(0xFF14AE5C)

        val ErrorPrimary = Color(0xFFFF4F4F)
        val PositivePrimary = Color(0xFF00B093)
        val Secondary = Color(0xFFAAAAAA)
        val SecondaryDimm = Color(0xFF676767)

        val ProgressBarPositive = Color(0xFFAFF4C6)
        val AIGradientCyan = Color(0xFF68F2FF)
        val AIGradientPink = Color(0xFFFF61ED)
        val AIBackground = Brush.verticalGradient(
            0.7f to AIGradientCyan,
            0.3f to AIGradientPink,
            tileMode = TileMode.Clamp
        )
    }
}