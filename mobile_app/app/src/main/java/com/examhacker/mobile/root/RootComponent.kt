package com.examhacker.mobile.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.router.stack.pushToFront
import kotlinx.serialization.Serializable
import com.examhacker.authentication.component.AuthenticationComponent
import com.examhacker.authentication.component.IAuthenticationComponent
import com.examhacker.common.utility.FilePicker
import com.examhacker.mobile.introduction_screen.IIntroductionComponent
import com.examhacker.mobile.introduction_screen.IntroductionComponent
import com.examhacker.mobile.util.IPermissionHandler
import com.examhacker.profile.component.IProfileComponent
import com.examhacker.profile.component.ProfileComponent
import com.examhacker.quiz_create.component.IQuizCreateComponent
import com.examhacker.quiz_create.component.QuizCreateComponent
import com.examhacker.quiz_edit.component.IQuizEditComponent
import com.examhacker.quiz_edit.component.QuizEditComponent
import com.examhacker.quiz_hub.component.IQuizHubComponent
import com.examhacker.quiz_hub.component.QuizHubComponent
import com.examhacker.quiz_info.component.IQuizInfoComponent
import com.examhacker.quiz_info.component.QuizInfoComponent
import com.examhacker.quiz_list.component.IQuizListComponent
import com.examhacker.quiz_solve.component.IQuizSolveComponent
import com.examhacker.settings.component.ISettingsComponent
import com.examhacker.quiz_list.component.QuizListComponent
import com.examhacker.quiz_solve.component.QuizSolveComponent
import com.examhacker.settings.component.SettingsComponent

interface IRootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class Introduction(val component: IIntroductionComponent) : Child()
        class Authentication(val component: IAuthenticationComponent) : Child()
        class QuizList(val component: IQuizListComponent) : Child()
        class QuizEdit(val component: IQuizEditComponent) : Child()
        class QuizInfo(val component: IQuizInfoComponent) : Child()
        class QuizCreate(val component: IQuizCreateComponent) : Child()
        class QuizHub(val component: IQuizHubComponent) : Child()
        class QuizSolve(val component: IQuizSolveComponent) : Child()
        class Profile(val component: IProfileComponent) : Child()
        class Settings(val component: ISettingsComponent) : Child()
    }
}

class RootComponent(
    private val componentContext: ComponentContext,
    private val permissionHandler: IPermissionHandler,
    private val filePicker: FilePicker,
    private val startOverlayService: () -> Unit
) : ComponentContext by componentContext, IRootComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, IRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Introduction,
            handleBackButton = false,
            childFactory = ::createChild,
        )
    private fun createChild(config: Config, componentContext: ComponentContext,)
    : IRootComponent.Child =
        when (config) {
            Config.Introduction   ->
                IRootComponent.Child.Introduction(
                    IntroductionComponent(
                        componentContext = componentContext,
                        permissionHandler = permissionHandler,
                        startOverlayService = startOverlayService,
                        goToAuth = ::fromIntroductionToAuth
                    )
                )

            Config.Authentication ->
                IRootComponent.Child.Authentication(
                    AuthenticationComponent(
                        componentContext = componentContext,
                        goToQuizList = ::fromAuthToQuizList,
                        goBack = ::back
                    )
                )

            Config.QuizList       ->
                IRootComponent.Child.QuizList(
                    QuizListComponent(
                        componentContext,
                        toQuizCreation = ::navigateToQuizCreation,
                        toQuizHub = {},
                        toProfile = {},
                        toSettings = {},
                        goBack = ::back
                    )
                )

            Config.QuizEdit       ->
                IRootComponent.Child.QuizEdit(
                    QuizEditComponent(componentContext)
                )

            Config.QuizCreate     ->
                IRootComponent.Child.QuizCreate(
                    QuizCreateComponent(
                        componentContext,
                        filePicker = filePicker,
                        back = ::back
                    )
                )

            Config.QuizInfo       ->
                IRootComponent.Child.QuizInfo(
                    QuizInfoComponent(componentContext)
                )

            Config.QuizHub        ->
                IRootComponent.Child.QuizHub(
                    QuizHubComponent(componentContext)
                )

            Config.QuizSolve      ->
                IRootComponent.Child.QuizSolve(
                    QuizSolveComponent(componentContext)
                )

            Config.Profile        ->
                IRootComponent.Child.Profile(
                    ProfileComponent(componentContext)
                )

            Config.Settings       ->
                IRootComponent.Child.Settings(
                    SettingsComponent(componentContext)
                )
        }

    private fun fromAuthToQuizList() {
        navigation.replaceCurrent(Config.QuizList)
    }

    private fun navigateToQuizCreation() {
        navigation.pushNew(Config.QuizCreate)
    }

    private fun navigateToQuizHub() {
        TODO()
    }

    private fun navigateToProfile() {
        TODO()
    }

    private fun navigateToSettings() {
        navigation.pushToFront(Config.Settings)
    }

    private fun fromIntroductionToAuth() {
        navigation.replaceCurrent(Config.Authentication)
    }

    private fun back() {
        navigation.pop()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Introduction : Config()
        @Serializable
        data object Authentication : Config()
        @Serializable
        data object QuizList : Config()
        @Serializable
        data object QuizEdit : Config()
        @Serializable
        data object QuizCreate : Config()
        @Serializable
        data object QuizInfo : Config()
        @Serializable
        data object QuizHub : Config()
        @Serializable
        data object QuizSolve : Config()
        @Serializable
        data object Profile : Config()
        @Serializable
        data object Settings : Config()
    }
}