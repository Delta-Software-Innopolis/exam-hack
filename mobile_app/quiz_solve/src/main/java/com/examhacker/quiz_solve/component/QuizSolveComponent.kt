package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext

interface IQuizSolveComponent {}

class QuizSolveComponent(componentContext: ComponentContext)
    : IQuizSolveComponent, ComponentContext by componentContext {

}