package com.examhacker.quiz_solve.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.examhacker.quiz_solve.component.AnswerItem
import com.examhacker.quiz_solve.component.AnswerState
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions



enum class AnswerState {
    DEFAULT,
    SELECTED,
    CORRECT,
    INCORRECT
}

@Composable
fun AnswerCard(
    answer: AnswerItem,
    onClick: () -> Unit = {}
) {

    val backgroundColor = when (answer.state) {
        AnswerState.CORRECT -> ColorPreset.BackgroundPositiveSecondary
        AnswerState.INCORRECT -> ColorPreset.BackgroundDangerSecondary
        else -> ColorPreset.BackgroundDefaultSecondary
    }

    val borderColor = when (answer.state) {
        AnswerState.SELECTED -> ColorPreset.BorderFocus
        AnswerState.CORRECT -> ColorPreset.BorderPositiveTertiary
        AnswerState.INCORRECT -> ColorPreset.BorderDangerTertiary
        else -> ColorPreset.BorderDefault
    }

    val borderWidth =
        if (answer.state == AnswerState.SELECTED)
            Dimensions.ThickBorderWidth
        else
            Dimensions.DefaultBorderWidth

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(Dimensions.AnswerVariantCardRadius)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(Dimensions.AnswerVariantCardRadius)
            )
            .clickable(onClick = onClick)
            .padding(Dimensions.ScreenPadding),
        contentAlignment = Alignment.CenterStart
    ) {

        Text(
            text = answer.text,
            fontSize = Dimensions.AnswerVariantFontSize,
            fontWeight = FontWeight.Normal
        )
    }
}