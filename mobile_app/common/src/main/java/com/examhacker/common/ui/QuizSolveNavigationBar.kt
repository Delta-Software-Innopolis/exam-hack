package com.examhacker.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun QuizSolveNavigationBar(
    onHintClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLeftClick: (() -> Unit)? = null,
    onRightClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
            if (onLeftClick != null && onRightClick != null)
                Arrangement.SpaceBetween
            else
                Arrangement.Center,
        modifier = modifier
    ) {
        onLeftClick?.let {
            QuizSolveNavigationButton(
                onClick = it,
                icon = painterResource(R.drawable.ic_chevron_arrow_left)
            )
        }

        QuizSolveNavigationButton(
            onClick = onHintClick,
            icon = painterResource(R.drawable.ic_gradient_star),
            borderStroke = BorderStroke(
                width = Dimensions.ThickBorderWidth,
                brush = ColorPreset.AIBackground
            ),
            iconModifier = Modifier
//                .size(Dimensions.NavigationButtonSize)
                .graphicsLayer {
                    compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
                }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = ColorPreset.AIBackground,
                        blendMode = BlendMode.SrcIn
                    )
                },
        )

        onRightClick?.let {
            QuizSolveNavigationButton(
                onClick = onRightClick,
                icon = painterResource(R.drawable.ic_chevron_arrow_right)
            )
        }
    }
}