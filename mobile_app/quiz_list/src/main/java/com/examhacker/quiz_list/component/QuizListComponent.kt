package com.examhacker.quiz_list.component

import com.arkivanov.decompose.ComponentContext

interface IQuizListComponent {}

class QuizListComponent(componentContext: ComponentContext)
    : IQuizListComponent, ComponentContext by componentContext {

}