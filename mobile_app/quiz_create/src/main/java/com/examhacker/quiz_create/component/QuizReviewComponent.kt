package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext

internal interface IQuizReviewComponent {}

internal class QuizReviewComponent(componentContext: ComponentContext)
    : IQuizReviewComponent, ComponentContext by componentContext {

}