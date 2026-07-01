package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext

interface IQuizCreateComponent {}

class QuizCreateComponent(componentContext: ComponentContext)
    : IQuizCreateComponent, ComponentContext by componentContext {

}