package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext

interface IQuizSolveComponent {
    val answerComponent: IQuizAnswerComponent
    val resultComponent: IQuizResultComponent
    var currentScreen: QuizSolveScreen
}

enum class QuizSolveScreen {
    ANSWER,
    RESULT
}

class QuizSolveComponent(
    componentContext: ComponentContext
) : IQuizSolveComponent,
    ComponentContext by componentContext {

    override var currentScreen = QuizSolveScreen.ANSWER

    override val answerComponent =
        QuizAnswerComponent(
            componentContext = componentContext,
            goBack = {
                // TODO
            },
            onPreviousQuestion = {
                // TODO
            },
            onNextQuestion = {
                currentScreen = QuizSolveScreen.RESULT
            },
            onOpenAiChat = {
                // TODO childSlot
            }
        )

    override val resultComponent =
        QuizResultComponent(
            componentContext = componentContext,
            onContinueGrinding = {
                currentScreen = QuizSolveScreen.ANSWER
            },
            onTakeBreak = {
                // TODO
            },
            goBack = {
                currentScreen = QuizSolveScreen.ANSWER
            }
        )
}