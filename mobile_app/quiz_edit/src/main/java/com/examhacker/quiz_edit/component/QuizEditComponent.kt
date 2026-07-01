package com.examhacker.quiz_edit.component

import com.arkivanov.decompose.ComponentContext

interface IQuizEditComponent {}

class QuizEditComponent(componentContext: ComponentContext)
    : IQuizEditComponent, ComponentContext by componentContext {

}