package com.examhacker.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun QuizProgressBar(
    solvedQuestions: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.ProgressBarSpacing),
        modifier = modifier
    ) {
        Text(
            text = "$solvedQuestions/$totalQuestions",
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = Dimensions.ProgressBarFontSize,
                color = ColorPreset.Black
            )
        )

        CustomProgressIndicator(
            progress = solvedQuestions.toFloat() / totalQuestions,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.ProgressBarHeight)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizProgressBarPreview() {
    QuizProgressBar(
        solvedQuestions = 1,
        totalQuestions = 2,
        modifier = Modifier.fillMaxWidth()
    )
}