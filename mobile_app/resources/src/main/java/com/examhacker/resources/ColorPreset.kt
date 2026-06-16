package com.examhacker.resources

import androidx.compose.ui.graphics.Color

class ColorPreset {
    companion object {
        val Black = Color(0xFF000000)
        val Transparent = Black.copy(alpha = 0f)

        val BackgroundDefaultPrimary = Color(0xFFFFFFFF)
        val BackgroundDefaultSecondary = Color(0xFFF5F5F5)
        val BackgroundPositiveSecondary = Color(0xFFCFF7D3)
        val BackgroundWarningSecondary = Color(0xFFFFF1C2)

        val BorderDefault = Color(0xFFD9D9D9)
        val BorderWarningTertiary = Color(0xFFBF6A02)

        val TextDefaultSecondary = Color(0xFF757575)
        val TextPositivePrimary = Color(0xFF02542D)
        val TextWarningTertiary = Color(0xFFBF6A02)

        val IconPositiveTertiary = Color(0xFF14AE5C)
    }
}