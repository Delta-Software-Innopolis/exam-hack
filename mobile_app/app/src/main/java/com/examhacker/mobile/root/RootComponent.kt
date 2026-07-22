package com.examhacker.mobile.root

import android.util.Log
import android.widget.Toast
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popToFirst
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.update
import kotlinx.serialization.Serializable
import com.examhacker.authentication.component.AuthenticationComponent
import com.examhacker.authentication.component.IAuthenticationComponent
import com.examhacker.common.utility.FilePicker
import com.examhacker.common.utility.IFileResolver
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

interface IRootComponent {

    val stack: Value<ChildStack<*, Child>>
    val model: StateFlow<Model>

    data class Model(
        val quizzes: List<Quiz>? = null
    )

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
    private val fileResolver: IFileResolver,
    private val startOverlayService: () -> Unit,
    private val showToast: (String) -> Unit
) : ComponentContext by componentContext, IRootComponent {

    private val _model = MutableStateFlow(IRootComponent.Model())
    override val model = _model

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
                        quizRepository = quizRepository,
                        quizStateFlow = model.map { it.quizzes },
                        saveQuizzes = ::saveQuizzes,
                        toQuizCreate = ::navigateToQuizCreate,
                        toQuizInfo = { quizId ->
                            val quiz = model.value.quizzes?.findLast { it.info.id == quizId }

                            quiz?.let { navigateToQuizInfo(quiz = it) }
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
                        quizId = config.quiz.info.id,
                        quizRepository = quizRepository,
                        showErrorToast = showToast,
                        saveQuiz = { questions ->
                            saveChangedQuiz(config.quiz.info.id, questions)
                        },
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
                        fileResolver = fileResolver,
                        quizRepository = quizRepository,
                        showErrorToast = showToast,
                        saveQuiz = ::saveQuiz,
                        toQuizHub = ::fromDeepQuizListToQuizHub,
                        toProfile = ::fromDeepQuizListToProfile,
                        toSettings = ::fromDeepQuizListToSettings,
                        goBack = {
                            Log.d("CreateDebugRoot", "Root stack: ${stack.items}")
                            back()
                        }
                    )
                )

            is Config.QuizInfo       ->
                IRootComponent.Child.QuizInfo(
                    QuizInfoComponent(
                        componentContext = componentContext,
                        quiz = config.quiz,
                        toSolve = { navigateToQuizSolve(config.quiz) },
                        toEdit = { navigateToQuizEdit(config.quiz) },
                        deleteQuiz = { deleteQuiz(config.quiz.info.id) },
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
                        authRepository = authRepository,
                        tokenStorage = tokenStorage,
                        showErrorToast = showToast,
                        toQuizHub = ::navigateToQuizHub,
                        toQuizList = ::navigateToQuizList,
                        toSettings = ::navigateToSettings,
                        toAuthentication = ::backToAuthentication,
                    )
                )

            is Config.Settings       ->
                IRootComponent.Child.Settings(
                    SettingsComponent(
                        componentContext = componentContext,
                        quizStateFlow = model.map { it.quizzes },
                        settingsStorage = settingStorage,
                        quizRepository = quizRepository,
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
        Log.d("DeleteDebug", "Quiz ID: ${quiz.info.id}")
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
    
    private fun backToAuthentication() {
        navigation.popToFirst()
        navigation.replaceCurrent(Config.Authentication)
    }

    private fun back() {
        navigation.pop()
    }

    private fun saveQuizzes(quizzes: List<Quiz>) {
        _model.update {
            it.copy(quizzes = quizzes)
        }
    }

    private fun deleteQuiz(quizId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            quizRepository.deletePack(quizId)
                .onSuccess {
                    val updatedQuizzes = model.value.quizzes?.toMutableList()
                    updatedQuizzes?.removeIf { it.info.id == quizId }

                    _model.update {
                        it.copy(quizzes = updatedQuizzes)
                    }

                    withContext(Dispatchers.Main) { back() }
                }
                .onFailure { exception ->
                    exception.message?.let {
                        withContext(Dispatchers.Main) { showToast(it) }
                    }
                }
        }
    }

    private fun saveChangedQuiz(quizId: Int, questions: List<Question>) {
        val updatedQuizzes = model.value.quizzes?.toMutableList()
        val index = updatedQuizzes?.indexOfFirst { it.info.id == quizId }
        index?.let { index ->
            val quizToUpdate = updatedQuizzes[index]
            updatedQuizzes.removeIf { it.info.id == quizId }
            updatedQuizzes.add(index, quizToUpdate.copy(questions = questions))
        }

        _model.update {
            it.copy(quizzes = updatedQuizzes)
        }
    }

    private fun saveQuiz(quiz: Quiz) {
        model.value.quizzes?.let { quizzes ->
            val newQuizzes = quizzes.toMutableList()
            newQuizzes.add(quiz)

            _model.update {
                it.copy(quizzes = newQuizzes)
            }
        }
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
                        hint = "Hint text",
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