package com.examhacker.quiz_edit.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface IQuizCreationComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Name(val component: IQuizNameComponent) : Child()
        data class Generate(val component: IQuizGenerateComponent) : Child()
        data class Edit(val component: IQuizEditComponent) : Child()
    }

    fun goBack()
    fun navigateToEdit()
}

class QuizCreationComponent(
    componentContext: ComponentContext,
    private val back: () -> Unit,
    private val onFinish: () -> Unit
) : IQuizCreationComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, IQuizCreationComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Name,
            handleBackButton = false,
            childFactory = ::createChild
        )

    private fun createChild(config: Config, componentContext: ComponentContext): IQuizCreationComponent.Child =
        when(config) {
            is Config.Name ->
                IQuizCreationComponent.Child.Name(
                    QuizNameComponent(
                        componentContext = componentContext,
                        onNext = ::navigateToGenerate
                    )
                )

            is Config.Generate ->
                IQuizCreationComponent.Child.Generate(
                    QuizGenerateComponent(
                        componentContext
                    )
                )

            is Config.Edit ->
                IQuizCreationComponent.Child.Edit(
                    QuizEditComponent(
                        componentContext
                    )
                )
        }

    private fun navigateToGenerate() {
        navigation.pushNew(Config.Generate)
    }

    override fun navigateToEdit() {
        navigation.pushNew(Config.Edit)
    }

    override fun goBack() {
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
        data object Edit: Config()
    }
}