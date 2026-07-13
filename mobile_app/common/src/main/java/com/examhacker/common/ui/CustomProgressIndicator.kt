package com.examhacker.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun CustomProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.drawWithCache {

            val leftCornerRadius = CornerRadius(
                x = Dimensions.PROGRESS_BAR_CORNER_RADIUS,
                y = Dimensions.PROGRESS_BAR_CORNER_RADIUS
            )

            val rightCornerRadius =
                if (progress >= PROGRESS_ROUNDING_START_POINT) {
                    val roundingCoefficient =
                        -1 * (PROGRESS_ROUNDING_START_POINT - progress) /
                            (1 - PROGRESS_ROUNDING_START_POINT)

                    CornerRadius(
                        x = Dimensions.PROGRESS_BAR_CORNER_RADIUS * roundingCoefficient,
                        y = Dimensions.PROGRESS_BAR_CORNER_RADIUS * roundingCoefficient
                    )
                } else {
                    CornerRadius.Zero
                }

            val progressPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            offset = Offset(0f, 0f),
                            size = size.copy(width = size.width * progress),
                        ),
                        topLeft = leftCornerRadius,
                        bottomLeft = leftCornerRadius,
                        topRight = rightCornerRadius,
                        bottomRight = rightCornerRadius
                    )
                )
            }

            onDrawBehind {
                drawRoundRect(
                    color = ColorPreset.BackgroundDefaultPrimary,
                    cornerRadius = CornerRadius(Dimensions.PROGRESS_BAR_CORNER_RADIUS)
                )

                drawPath(
                    path = progressPath,
                    color = ColorPreset.PositivePrimary
                )
            }
        }
    )
}

private const val PROGRESS_ROUNDING_START_POINT = 0.985f

@Preview(showBackground = true)
@Composable
private fun CustomProgressIndicatorPreview() {
    CustomProgressIndicator(
        progress = 0.995f,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.ProgressBarHeight)
    )
}