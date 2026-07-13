package com.examhacker.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun QuizSolveBottomBar(
    onAiClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPreviousClick: (() -> Unit)? = null,
    onNextClick: (() -> Unit)? = null,
    previousEnabled: Boolean = false,
    nextEnabled: Boolean = false
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {

        onPreviousClick?.let {
            QuizSolveIconButton(
                painter = painterResource(R.drawable.ic_chevron_arrow_left),
                onClick = it,
                enabled = previousEnabled,
                modifier = Modifier.padding(Dimensions.NavigationButtonPadding)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        QuizSolveAiButton(onClick = onAiClick)

        onNextClick?.let {
            Spacer(modifier = Modifier.weight(1f))

            QuizSolveIconButton(
                painter = painterResource(R.drawable.ic_chevron_arrow_right),
                onClick = onNextClick,
                enabled = nextEnabled,
                modifier = Modifier.padding(Dimensions.NavigationButtonPadding)
            )
        }
    }
}

@Composable
private fun QuizSolveIconButton(
    painter: Painter,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = ColorPreset.Transparent,
            disabledContainerColor = ColorPreset.Transparent,
            contentColor = ColorPreset.SecondaryDimm,
            disabledContentColor = ColorPreset.Secondary
        )
    ) {
        Icon(
            painter = painter,
            contentDescription = "",
            modifier = Modifier.size(Dimensions.NavigationIconSize)
        )
    }
}

@Composable
private fun QuizSolveAiButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickable { onClick() }
            .border(
                width = Dimensions.ThickBorderWidth,
                brush = ColorPreset.AIBackground,
                shape = RoundedCornerShape(Dimensions.ButtonRadius)
            )
            .padding(Dimensions.NavigationButtonPadding)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_gradient_star),
            contentDescription = ""
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizSolveBottomBarPreview() {
    QuizSolveBottomBar(
        onPreviousClick = {},
        onAiClick = {},
        onNextClick = {}
    )
}