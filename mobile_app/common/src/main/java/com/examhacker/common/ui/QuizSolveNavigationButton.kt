package com.examhacker.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun QuizSolveNavigationButton(
    onClick: () -> Unit,
    icon: Painter,
    iconModifier: Modifier = Modifier,
    borderStroke: BorderStroke? = null
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPreset.BackgroundDefaultPrimary
        ),
        border = borderStroke,
        contentPadding = PaddingValues(Dimensions.NavigationButtonPadding)
    ) {
        Icon(
            painter = icon,
            contentDescription = "",
            modifier = iconModifier
        )
    }
}