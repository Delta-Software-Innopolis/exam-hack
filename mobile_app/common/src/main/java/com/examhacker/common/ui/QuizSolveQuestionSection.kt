package com.examhacker.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import com.examhacker.resources.Dimensions

@Composable
fun QuizSolveQuestionSection(
    question: Question,
    finalAnswer: AnswerVariant?,
    onAnswerClick: (AnswerVariant) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.DescriptionVariantsSpacing),
        modifier = modifier
    ) {
        Text(
            text = question.description,
            style = TextStyle(
                fontSize = Dimensions.QuestionDescriptionFontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.padding(Dimensions.ScreenPadding)
        )

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.VariantsSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(question.variants) {
                AnswerVariantCard(
                    description = it.description,
                    onClick = { onAnswerClick(it) },
                    enabled = finalAnswer == null,
                    status =
                        if (finalAnswer == null || (finalAnswer != it && !it.isCorrect))
                            AnswerVariantStatus.DEFAULT
                        else if (it.isCorrect)
                            AnswerVariantStatus.ANSWERED_CORRECT
                        else
                            AnswerVariantStatus.ANSWERED_WRONG,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.VariantsSpacing)
                )
            }
        }
    }
}