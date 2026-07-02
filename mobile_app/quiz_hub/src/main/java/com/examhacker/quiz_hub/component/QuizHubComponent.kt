package com.examhacker.quiz_hub.component

import com.arkivanov.decompose.ComponentContext

interface IQuizHubComponent {}

class QuizHubComponent(componentContext: ComponentContext)
    : IQuizHubComponent, ComponentContext by componentContext {

}