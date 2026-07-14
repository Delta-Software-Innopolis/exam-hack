package com.examhacker.mobile.root

import android.util.Log
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.router.stack.pushToFront
import kotlinx.serialization.Serializable
import com.examhacker.authentication.component.AuthenticationComponent
import com.examhacker.authentication.component.IAuthenticationComponent
import com.examhacker.common.utility.FilePicker
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Author
import com.examhacker.domain.model.Question
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.model.QuizInfo
import com.examhacker.domain.repository.IAuthenticationRepository
import com.examhacker.domain.repository.IQuizRepository
import com.examhacker.domain.repository.ITokenStorage
import com.examhacker.common.utility.ISettingStorage
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
import kotlinx.serialization.Contextual
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

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
    private val tokenStorage: ITokenStorage,
    private val authRepository: IAuthenticationRepository,
    private val quizRepository: IQuizRepository,
    private val permissionHandler: IPermissionHandler,
    private val settingStorage: ISettingStorage,
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
    @OptIn(ExperimentalTime::class)
    private fun createChild(config: Config, componentContext: ComponentContext,)
    : IRootComponent.Child =
        when (config) {
            is Config.Introduction   ->
                IRootComponent.Child.Introduction(
                    IntroductionComponent(
                        componentContext = componentContext,
                        permissionHandler = permissionHandler,
                        startOverlayService = startOverlayService,
                        goToAuth = ::fromIntroductionToAuth
                    )
                )

            is Config.Authentication ->
                IRootComponent.Child.Authentication(
                    AuthenticationComponent(
                        componentContext = componentContext,
                        authRepository = authRepository,
                        tokenStorage = tokenStorage,
                        goToQuizList = ::fromAuthToQuizList,
                        goBack = ::back
                    )
                )

            is Config.QuizList       ->
                IRootComponent.Child.QuizList(
                    QuizListComponent(
                        componentContext,
                        quizzes = createMockQuizList(),
                        toQuizCreate = ::navigateToQuizCreate,
                        toQuizInfo = {
                            navigateToQuizInfo(
                                Quiz(
                                    info = QuizInfo(
                                        id = 1,
                                        name = "Quiz name",
                                        creationDate = now().toString(),
                                        updatingDate = null,
                                        author = Author(1, "User")
                                    ),
                                    description = "Nice description",
                                    questions = listOf(
                                        Question(
                                            id = 1,
                                            description = "How much?",
                                            variants = listOf(
                                                AnswerVariant("One", false),
                                                AnswerVariant("Two", true)
                                            )
                                        )
                                    )
                                )
                            )
                        },
                        toQuizHub = ::navigateToQuizHub,
                        toProfile = ::navigateToProfile,
                        toSettings = ::navigateToSettings,
                        goBack = ::back
                    )
                )

            is Config.QuizEdit       ->
                IRootComponent.Child.QuizEdit(
                    QuizEditComponent(
                        componentContext = componentContext,
                        questions = config.quiz.questions,
                        saveQuiz = { },
                        toQuizHub = ::fromDeepQuizListToQuizHub,
                        toProfile = ::fromDeepQuizListToProfile,
                        toSettings = ::fromDeepQuizListToSettings,
                        back = ::back,
                    )
                )

            is Config.QuizCreate     ->
                IRootComponent.Child.QuizCreate(
                    QuizCreateComponent(
                        componentContext,
                        filePicker = filePicker,
                        toQuizHub = ::fromDeepQuizListToQuizHub,
                        toProfile = ::fromDeepQuizListToProfile,
                        toSettings = ::fromDeepQuizListToSettings,
                        back = ::back
                    )
                )

            is Config.QuizInfo       ->
                IRootComponent.Child.QuizInfo(
                    QuizInfoComponent(
                        componentContext = componentContext,
                        quiz = config.quiz,
                        toSolve = { navigateToQuizSolve(config.quiz) },
                        toEdit = { navigateToQuizEdit(config.quiz) },
                        deleteQuiz = { back() },
                        toQuizHub = ::fromDeepQuizListToQuizHub,
                        toProfile = ::fromDeepQuizListToProfile,
                        toSettings = ::fromDeepQuizListToSettings,
                        back = ::back,
                    )
                )

            is Config.QuizHub        ->
                IRootComponent.Child.QuizHub(
                    QuizHubComponent(
                        componentContext = componentContext,
                        toQuizList = ::navigateToQuizList,
                        toProfile = ::navigateToProfile,
                        toSettings = ::navigateToSettings
                    )
                )

            is Config.QuizSolve      ->
                IRootComponent.Child.QuizSolve(
                    QuizSolveComponent(
                        componentContext = componentContext,
                        questions = config.quiz.questions,
                        goBack = ::back
                    )
                )

            is Config.Profile        ->
                IRootComponent.Child.Profile(
                    ProfileComponent(
                        componentContext = componentContext,
                        toQuizHub = ::navigateToQuizHub,
                        toQuizList = ::navigateToQuizList,
                        toSettings = ::navigateToSettings
                    )
                )

            is Config.Settings       ->
                IRootComponent.Child.Settings(
                    SettingsComponent(
                        componentContext = componentContext,
                        settingsStorage = settingStorage,
                        goToQuizList = ::navigateToQuizList,
                        goToProfile = ::navigateToProfile,
                        goToQuizHub = ::navigateToQuizHub,
                    )
                )
        }

    private fun fromAuthToQuizList() {
        navigation.replaceCurrent(Config.QuizList)
    }

    private fun navigateToQuizCreate() {
        navigation.pushNew(Config.QuizCreate)
    }

    private fun navigateToQuizHub() {
        navigation.pushToFront(Config.QuizHub)
    }

    private fun navigateToProfile() {
        navigation.pushToFront(Config.Profile)
    }

    private fun navigateToSettings() {
        navigation.pushToFront(Config.Settings)
    }

    private fun navigateToQuizList() {
        navigation.pushToFront(Config.QuizList)
    }

    private fun navigateToQuizSolve(quiz: Quiz) {
        navigation.pushNew(Config.QuizSolve(quiz))
    }

    private fun navigateToQuizEdit(quiz: Quiz) {
        navigation.pushNew(Config.QuizEdit(quiz))
    }

    private fun fromIntroductionToAuth() {
        navigation.replaceCurrent(Config.Authentication)
    }

    private fun navigateToQuizInfo(quiz: Quiz) {
        navigation.pushNew(Config.QuizInfo(quiz))
    }

    private fun fromDeepQuizListToQuizHub() {
        navigation.popWhile { it !is Config.QuizList }
        navigateToQuizHub()
    }

    private fun fromDeepQuizListToProfile() {
        navigation.popWhile { it !is Config.QuizList }
        navigateToProfile()
    }

    private fun fromDeepQuizListToSettings() {
        navigation.popWhile { it !is Config.QuizList }
        navigateToSettings()
    }

    private fun back() {
        navigation.pop()
    }

    @OptIn(ExperimentalTime::class)
    private fun createMockQuizList(): List<Quiz> =
        List(8) { index ->
            Quiz(
                info = QuizInfo(
                    id = index,
                    name = "Quiz name",
                    creationDate = now().toString(),
                    updatingDate = now().toString(),
                    author = Author(
                        id = index,
                        name = "User"
                    )
                ),
                description = "Quiz description",
                questions = List(5) {
                    Question(
                        id = index,
                        description = "What is the name of your Practicum Project TA?",
                        variants = listOf(
                            AnswerVariant("Andrei Markov", true),
                            AnswerVariant("other", false)
                        )
                    )
                }
            )
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
        data class QuizEdit(@Contextual val quiz: Quiz) : Config()
        @Serializable
        data object QuizCreate : Config()
        @Serializable
        data class QuizInfo(@Contextual val quiz: Quiz) : Config() // Replace with quiz ID, when connect to backend and local db
        @Serializable
        data object QuizHub : Config()
        @Serializable
        data class QuizSolve(@Contextual val quiz: Quiz) : Config()
        @Serializable
        data object Profile : Config()
        @Serializable
        data object Settings : Config()
    }
}