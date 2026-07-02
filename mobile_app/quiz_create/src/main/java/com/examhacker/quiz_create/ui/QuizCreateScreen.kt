package com.examhacker.quiz_create.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.examhacker.quiz_create.component.IQuizCreateComponent

@Composable
fun QuizCreateScreen(component: IQuizCreateComponent) {
    Children(
        stack = component.stack,
        modifier = Modifier.fillMaxSize(),
    ) {
        when(val child = it.instance) {
            is IQuizCreateComponent.Child.Name     -> QuizNameScreen(child.component)
            is IQuizCreateComponent.Child.Generate -> QuizGenerateScreen(child.component)
            is IQuizCreateComponent.Child.Edit     -> QuizEditScreen(child.component)
        }
    }
}