package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface IQuizCreateComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Name(val component: IQuizNameComponent) : Child()
        data class Generate(val component: IQuizGenerateComponent) : Child()
        data class Edit(val component: IQuizEditComponent) : Child()
    }

    fun goBack()
    fun navigateToEdit()
}

class QuizCreateComponent(
    componentContext: ComponentContext,
    private val back: () -> Unit,
    private val onFinish: () -> Unit
) : IQuizCreateComponent, ComponentContext by componentContext {

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
                        onNext = ::navigateToGenerate
                    )
                )

            is Config.Generate ->
                IQuizCreateComponent.Child.Generate(
                    QuizGenerateComponent(
                        componentContext
                    )
                )

            is Config.Edit ->
                IQuizCreateComponent.Child.Edit(
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