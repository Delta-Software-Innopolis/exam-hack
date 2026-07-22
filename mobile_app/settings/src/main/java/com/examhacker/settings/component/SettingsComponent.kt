package com.examhacker.settings.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.model.Question
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.common.utility.ISettingStorage
import com.examhacker.domain.model.Author
import com.examhacker.domain.model.QuizInfo
import com.examhacker.domain.repository.IQuizRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

interface ISettingsComponent {
    val model: Value<Model>

    data class Model(
        val isPhoneUnlockFeatureOn: Boolean = false,
        val isEnglishLanguage: Boolean = true,
        val isLightTheme: Boolean = true,
        val quizzes: List<Quiz>? = null,
        val selectedQuiz: Quiz? = null,
        val isQuizzesLoading: Boolean = false,
        val quizLoadingError: String? = null
    )

    fun onPhoneUnlockFeatureToggle()
    fun onQuizSelect(id: Int)
    fun onLanguageToggle()
    fun onThemeToggle()
    fun toProfile()
    fun toQuizList()
    fun toQuizHub()
}

class SettingsComponent(
    componentContext: ComponentContext,
    private val settingsStorage: ISettingStorage,
    private val quizRepository: IQuizRepository,
    private val goToQuizList: () -> Unit,
    private val goToProfile: () -> Unit,
    private val goToQuizHub: () -> Unit
) : ISettingsComponent, ComponentContext by componentContext {

    private val _model = MutableValue(ISettingsComponent.Model())
    override val model = _model

    init {
        val isUnlockFeatureOn = settingsStorage.getUnlockFeatureMode()
        _model.update {
            it.copy(isPhoneUnlockFeatureOn = isUnlockFeatureOn)
        }

        if (isUnlockFeatureOn) {
            loadQuizzes()
        }
    }

    override fun onPhoneUnlockFeatureToggle() {
        if (model.value.isPhoneUnlockFeatureOn) {
            CoroutineScope(Dispatchers.Main).launch {
                    settingsStorage.saveUnlockFeatureMode(false)
                    settingsStorage.clearUnlockFeatureQuiz()
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                settingsStorage.saveUnlockFeatureMode(true)
            }
        }

        _model.update {
            it.copy(isPhoneUnlockFeatureOn = !it.isPhoneUnlockFeatureOn)
        }

        if (model.value.isPhoneUnlockFeatureOn) {
            loadQuizzes()
        }
    }

    override fun onQuizSelect(id: Int) {
        val quiz = model.value.quizzes?.findLast { it.info.id == id }

        quiz?.let { quiz ->
            CoroutineScope(Dispatchers.IO).launch {
                settingsStorage.saveUnlockFeatureQuiz(quiz)
            }

            _model.update {
                it.copy(selectedQuiz = quiz)
            }
        }
    }

    override fun onLanguageToggle() {
        _model.update {
            it.copy(isEnglishLanguage = !it.isEnglishLanguage)
        }
    }

    override fun onThemeToggle() {
        _model.update {
            it.copy(isLightTheme = !it.isLightTheme)
        }
    }

    override fun toProfile() {
        goToProfile()
    }

    override fun toQuizList() {
        goToQuizList()
    }

    override fun toQuizHub() {
        goToQuizHub()
    }

    private fun loadQuizzes() {
        CoroutineScope(Dispatchers.IO).launch {
            _model.update {
                it.copy(isQuizzesLoading = true)
            }

            quizRepository.getAllPacks()
                .onSuccess { quizzes ->

                    val selectedQuizWithAnswers = settingsStorage.getUnlockFeatureQuiz()
                    _model.update {
                        it.copy(
                            quizzes = quizzes,
                            selectedQuiz = selectedQuizWithAnswers?.first,
                            isQuizzesLoading = false
                        )
                    }
                }
                .onFailure { exception ->

                    _model.update {
                        it.copy(
                            isQuizzesLoading = false,
                            quizLoadingError = exception.message
                        )
                    }
                }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createMockQuizzes(): List<Quiz> =
        listOf(
            Quiz(
                info = QuizInfo(
                    id = 1,
                    name = "best quiz",
                    creationDate = now().toString(),
                    updatingDate = null,
                    author = Author(
                        id = 1,
                        name = "pavmash"
                    )
                ),
                description = "",
                questions = listOf(
                    Question(
                        id = 1,
                        description = "How much?",
                        hint = "Hint text",
                        variants = listOf(
                            AnswerVariant("One", false),
                            AnswerVariant("Two", true)
                        )
                    ),
                    Question(
                        id = 2,
                        hint = "Hint text",
                        description = "How are you",
                        variants = listOf(AnswerVariant("good", true))
                    )
                )
            ),
            Quiz(
                info = QuizInfo(
                    id = 1,
                    name = "Nice quiz",
                    creationDate = now().toString(),
                    updatingDate = null,
                    author = Author(
                        id = 2,
                        name = "upconett"
                    )
                ),
                description = "",
                questions = listOf(
                    Question(
                        id = 3,
                        description = "Hate me?",
                        hint = "Hint text",
                        variants = listOf(AnswerVariant("No", true))
                    )
                )
            )
        )
}