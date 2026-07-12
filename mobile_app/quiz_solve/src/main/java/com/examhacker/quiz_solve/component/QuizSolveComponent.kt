package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.data.Question
import kotlinx.serialization.Serializable

interface IQuizSolveComponent {
    val stack: Value<ChildStack<*, Child>>
    val model: Value<Model>

    data class Model(
        val questions: List<Question> = emptyList(),
        val correctCount: Int = 0,
    )

    sealed class Child {
        data class QuizQuestion(val component: IQuizQuestionComponent) : Child()
        data class QuizResult(val component: IQuizResultComponent) : Child()
    }
}

class QuizSolveComponent(
    componentContext: ComponentContext,
    questions: List<Question>,
    private val goBack: () -> Unit,
) : IQuizSolveComponent, ComponentContext by componentContext {

    private val _model =  MutableValue(IQuizSolveComponent.Model())
    override val model = _model

    init {
        _model.update {
            it.copy(
                questions = questions,
                correctCount = 0
            )
        }
    }

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
                        questions = model.value.questions,
                        updateResults = ::updateResults,
                        goToResults = ::navigateToResults,
                        goBack = ::back
                    )
                )

            is Config.QuizResult   ->
                IQuizSolveComponent.Child.QuizResult(
                    QuizResultComponent(
                        correctAnswerCount = model.value.correctCount,
                        questionCount = model.value.questions.size,
                        componentContext = componentContext,
                        onQuitSolving = ::onQuitSolving,
                        goBack = ::back
                    )
                )
        }

    private fun updateResults(correctCount: Int) {
        _model.update {
            it.copy(correctCount = correctCount)
        }
    }

    private fun navigateToResults() {
        navigation.pushNew(Config.QuizResult)
    }

    private fun onQuitSolving() {
        goBack()
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