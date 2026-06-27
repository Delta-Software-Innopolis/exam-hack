package com.examhacker.quiz_edit.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.examhacker.quiz_edit.component.IQuizCreationComponent

@Composable
fun QuizCreationScreen(component: IQuizCreationComponent) {
    Children(
        stack = component.stack,
        modifier = Modifier.fillMaxSize(),
    ) {
        when(val child = it.instance) {
            is IQuizCreationComponent.Child.Name     -> QuizNameScreen(child.component)
            is IQuizCreationComponent.Child.Generate -> QuizGenerateScreen(child.component)
            is IQuizCreationComponent.Child.Edit     -> QuizEditScreen(child.component)
        }
    }
}