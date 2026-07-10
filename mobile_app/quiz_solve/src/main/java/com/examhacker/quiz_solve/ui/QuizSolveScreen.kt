package com.examhacker.quiz_solve.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.examhacker.quiz_solve.component.IQuizSolveComponent

@Composable
fun QuizSolveScreen(component: IQuizSolveComponent) {
    Children(
        component.stack,
        Modifier.fillMaxSize()
    ) {
        when(val child = it.instance) {
            is IQuizSolveComponent.Child.QuizQuestion -> QuizQuestionScreen(child.component)
            is IQuizSolveComponent.Child.QuizResult   -> QuizResultScreen(child.component)
        }
    }
}