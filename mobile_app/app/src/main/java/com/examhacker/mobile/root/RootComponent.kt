package com.examhacker.mobile.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable
import com.examhacker.authentication.component.AuthenticationComponent
import com.examhacker.authentication.component.IAuthenticationComponent
import com.examhacker.ai_interactions.component.AIGenerationComponent
import com.examhacker.ai_interactions.component.IAIGenerationComponent
import com.examhacker.quiz_edit.component.IQuizEditComponent
import com.examhacker.quiz_list.component.IQuizListComponent
import com.examhacker.quiz_solve.component.IQuizSolveComponent
import com.examhacker.settings.component.ISettingsComponent
import com.examhacker.quiz_edit.component.QuizEditComponent
import com.examhacker.quiz_list.component.QuizListComponent
import com.examhacker.quiz_solve.component.QuizSolveComponent
import com.examhacker.settings.component.SettingsComponent

interface IRootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class Authentication(val component: IAuthenticationComponent) : Child()
        class AIInteractions(val component: IAIGenerationComponent) : Child()
        class QuizList(val component: IQuizListComponent) : Child()
        class QuizEdit(val component: IQuizEditComponent) : Child()
        class QuizSolve(val component: IQuizSolveComponent) : Child()
        class Settings(val component: ISettingsComponent) : Child()
    }
}

class RootComponent(private val componentContext: ComponentContext)
    : ComponentContext by componentContext, IRootComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, IRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Authentication,
            handleBackButton = false,
            childFactory = ::createChild,
        )

    private fun createChild(config: Config, componentContext: ComponentContext,)
    : IRootComponent.Child =
        when (config) {
            Config.Authentication ->
                IRootComponent.Child.Authentication(
                    AuthenticationComponent(componentContext)
                )

            Config.AIInteractions ->
                IRootComponent.Child.AIInteractions(
                    AIGenerationComponent(componentContext)
                )

            Config.QuizList ->
                IRootComponent.Child.QuizList(
                    QuizListComponent(componentContext)
                )

            Config.QuizEdit ->
                IRootComponent.Child.QuizEdit(
                    QuizEditComponent(componentContext)
                )

            Config.QuizSolve ->
                IRootComponent.Child.QuizSolve(
                    QuizSolveComponent(componentContext)
                )

            Config.Settings ->
                IRootComponent.Child.Settings(
                    SettingsComponent(componentContext)
                )
        }

    @Serializable
    sealed class Config {
        @Serializable
        data object Authentication : Config()
        @Serializable
        data object AIInteractions : Config()
        @Serializable
        data object QuizList : Config()
        @Serializable
        data object QuizEdit : Config()
        @Serializable
        data object QuizSolve : Config()
        @Serializable
        data object Settings : Config()
    }

}