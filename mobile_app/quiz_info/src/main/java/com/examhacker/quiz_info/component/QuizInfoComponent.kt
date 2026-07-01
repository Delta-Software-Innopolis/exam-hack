package com.examhacker.quiz_info.component

import com.arkivanov.decompose.ComponentContext

interface IQuizInfoComponent {}

class QuizInfoComponent(componentContext: ComponentContext)
    : IQuizInfoComponent, ComponentContext by componentContext {

}