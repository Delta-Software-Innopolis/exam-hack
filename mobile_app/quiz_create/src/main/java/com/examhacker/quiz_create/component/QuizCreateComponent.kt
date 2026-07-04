package com.examhacker.quiz_create.component

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
import com.examhacker.common.data.Quiz
import kotlinx.serialization.Serializable

interface IQuizCreateComponent {
    val stack: Value<ChildStack<*, Child>>
    val model: Value<Model>

    data class Model(
        val quiz: Quiz = Quiz("", "", emptyList())
    )

    sealed class Child {
        internal data class Name(val component: IQuizNameComponent) : Child()
        internal data class Generate(val component: IQuizGenerateComponent) : Child()
        internal data class Edit(val component: IQuizReviewComponent) : Child()
    }
}

class QuizCreateComponent(
    componentContext: ComponentContext,
    private val back: () -> Unit,
    private val onFinish: () -> Unit
) : IQuizCreateComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizCreateComponent.Model())
    override val model = _model

    private val navigation = StackNavigation<Config>()
    override val stack: Value<ChildStack<*, IQuizCreateComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Name,
            handleBackButton = false,
            childFactory = ::createChild
        )

    private fun createChild(config: Config, componentContext: ComponentContext): IQuizCreateComponent.Child =
        when(config) {
            is Config.Name ->
                IQuizCreateComponent.Child.Name(
                    QuizNameComponent(
                        componentContext = componentContext,
                        goToGenerate = ::navigateToGenerate,
                        updateName = ::updateQuizName,
                        updateDescription = ::updateQuizDescription
                    )
                )

            is Config.Generate ->
                IQuizCreateComponent.Child.Generate(
                    QuizGenerateComponent(
                        componentContext
                    )
                )

            is Config.Review ->
                IQuizCreateComponent.Child.Edit(
                    QuizReviewComponent(
                        componentContext
                    )
                )
        }

    private fun updateQuizName(name: String) {
        _model.update {
            it.copy(
                quiz = it.quiz.copy(name = name)
            )
        }
    }

    private fun updateQuizDescription(description: String) {
        _model.update {
            it.copy(
                quiz = it.quiz.copy(description = description)
            )
        }
    }

    private fun navigateToGenerate() {
        navigation.pushNew(Config.Generate)
    }

    private fun navigateToReview() {
        navigation.pushNew(Config.Review)
    }

    private fun goBack() {
        if (stack.items.size > 1) {
            navigation.pop()
        } else {
            back()
        }
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Name: Config()
        @Serializable
        data object Generate: Config()
        @Serializable
        data object Review: Config()
    }
}