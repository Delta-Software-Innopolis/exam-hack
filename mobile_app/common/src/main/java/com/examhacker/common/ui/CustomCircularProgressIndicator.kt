package com.examhacker.common.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun CustomCircularProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(Dimensions.CircularProgressIndicatorSize),
        color = ColorPreset.Secondary,
        strokeWidth = Dimensions.CircularProgressIndicatorWidth,
        trackColor = ColorPreset.BackgroundVariant,
        strokeCap = StrokeCap.Round
    )
}