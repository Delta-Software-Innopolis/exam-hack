package com.examhacker.quiz_solve.ui

import androidx.compose.runtime.Composable
import com.examhacker.quiz_solve.component.IQuizSolveComponent
import com.examhacker.quiz_solve.component.QuizSolveScreen as QuizSolveState

@Composable
fun QuizSolveScreen(
    component: IQuizSolveComponent
) {
    when (component.currentScreen) {

        QuizSolveState.ANSWER ->
            QuizQuestionScreen(component.answerComponent)

        QuizSolveState.RESULT ->
            QuizResultScreen(component.resultComponent)
    }
}