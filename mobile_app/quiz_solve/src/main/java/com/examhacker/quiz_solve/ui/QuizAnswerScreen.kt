package com.examhacker.quiz_solve.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.examhacker.quiz_solve.component.IQuizAnswerComponent
import com.examhacker.resources.Dimensions
import androidx.compose.foundation.layout.Column

@Composable
fun QuizAnswerScreen(
    component: IQuizAnswerComponent
) {

    val uiState = component.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(
            Dimensions.DescriptionVariantsSpacing
        )
    ) {

        QuizProgressBar(
            currentQuestion = uiState.currentQuestion,
            totalQuestions = uiState.totalQuestions
        )

        // TODO Question description

        Column(
            verticalArrangement = Arrangement.spacedBy(
                Dimensions.VariantsSpacing
            )
        ) {

            uiState.answers.forEach { answer ->

                AnswerCard(
                    text = answer.text,
                    state = answer.state
                )

            }
        }

        // TODO Bottom bar
    }
}