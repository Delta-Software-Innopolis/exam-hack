package com.examhacker.quiz_solve.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun QuizSolveBottomBar(
    onPreviousClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimensions.QuizSolveBottomBarHorizontalPadding,
                vertical = Dimensions.QuizSolveBottomBarBottomPadding
            ),
        horizontalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.icon_button_left),
            contentDescription = null,
            modifier = Modifier
                .size(Dimensions.QuizSolveBottomBarIconSize)
                .clickable(onClick = onPreviousClick)
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.ai_button),
            contentDescription = null,
            modifier = Modifier
                .size(Dimensions.QuizSolveBottomBarAiButtonSize)
                .clickable(onClick = onAiClick)
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.icon_button_right),
            contentDescription = null,
            modifier = Modifier
                .size(Dimensions.QuizSolveBottomBarIconSize)
                .clickable(onClick = onNextClick)
        )
    }
}