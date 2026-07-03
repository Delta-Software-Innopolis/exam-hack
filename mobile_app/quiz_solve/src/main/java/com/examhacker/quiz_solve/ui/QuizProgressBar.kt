package com.examhacker.quiz_solve.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun QuizProgressBar(
    currentQuestion: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {

    val progress =
        if (totalQuestions == 0)
            0f
        else
            currentQuestion.toFloat() / totalQuestions.toFloat()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.ProgressBarSpacing)
    ) {

        Text(
            text = "$currentQuestion / $totalQuestions",
            fontSize = Dimensions.ProgressBarFontSize,
            fontWeight = FontWeight.Normal,
            color = ColorPreset.Black
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = Dimensions.ScreenPadding),
            color = ColorPreset.ProgressBarPositive,
            trackColor = ColorPreset.BackgroundDefaultSecondary
        )
    }
}