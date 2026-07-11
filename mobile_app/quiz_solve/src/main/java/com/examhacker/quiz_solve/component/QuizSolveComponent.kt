package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.examhacker.common.data.Quiz
import kotlinx.serialization.Serializable

interface IQuizSolveComponent {
    val stack: Value<ChildStack<*, Child>>
    val model: Value<Model>

    data class Model(
        val quiz: Quiz? = null,
        val correctCount: Int = 0
    )

    sealed class Child {
        data class QuizQuestion(val component: IQuizQuestionComponent) : Child()
        data class QuizResult(val component: IQuizResultComponent) : Child()
    }
}

class QuizSolveComponent(
    componentContext: ComponentContext,
    private val goBack: () -> Unit,
) : IQuizSolveComponent, ComponentContext by componentContext {

    private val _model =  MutableValue(IQuizSolveComponent.Model())
    override val model = _model

    private val navigation = StackNavigation<Config>()
    override val stack: Value<ChildStack<*, IQuizSolveComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.QuizQuestion,
            handleBackButton = false,
            childFactory = ::createChild
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): IQuizSolveComponent.Child =
        when(config) {
            is Config.QuizQuestion ->
                IQuizSolveComponent.Child.QuizQuestion(
                    QuizQuestionComponent(
                        componentContext = componentContext,
                        goBack = ::back,
                        onOpenAiChat = {}
                    )
                )

            is Config.QuizResult   ->
                IQuizSolveComponent.Child.QuizResult(
                    QuizResultComponent(
                        correctAnswerCount = 4,
                        questionCount = 5,
                        componentContext = componentContext,
                        onQuitSolving = {},
                        goBack = ::back
                    )
                )
        }

    private fun back() {
        if (stack.items.size > 1) {
            navigation.pop()
        } else {
            goBack()
        }
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object QuizQuestion : Config()
        @Serializable
        data object QuizResult : Config()
    }
}