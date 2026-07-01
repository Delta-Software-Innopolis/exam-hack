package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext

interface IQuizEditComponent {}

class QuizEditComponent(componentContext: ComponentContext)
    : IQuizEditComponent, ComponentContext by componentContext {

}