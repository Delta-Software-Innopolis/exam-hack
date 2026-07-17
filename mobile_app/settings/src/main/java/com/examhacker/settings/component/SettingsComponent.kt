package com.examhacker.settings.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import com.examhacker.common.data.Quiz
import com.examhacker.common.utility.ISettingStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    private val goToQuizList: () -> Unit,
    private val goToProfile: () -> Unit,
    private val goToQuizHub: () -> Unit
) : ISettingsComponent, ComponentContext by componentContext {

    private val _model = MutableValue(ISettingsComponent.Model())
    override val model = _model

    override fun onPhoneUnlockFeatureToggle() {
        _model.update {
            it.copy(
                isPhoneUnlockFeatureOn = !it.isPhoneUnlockFeatureOn,
                quizzes =
                    if (it.quizzes == null)
                        createMockQuizzes()
                    else
                        null,
                selectedQuiz = settingsStorage.getUnlockFeatureQuiz()?.first
            )
        }
    }

    override fun onQuizSelect(id: Int) {
        val quiz = model.value.quizzes?.findLast { it.id == id }

        quiz?.let {
            CoroutineScope(Dispatchers.IO).launch {
                settingsStorage.saveUnlockFeatureQuiz(it)
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

    private fun createMockQuizzes(): List<Quiz> =
        listOf(
            Quiz(
                id = 1,
                authorName = "pavmash",
                name = "best quiz",
                description = "",
                questions = listOf(
                    Question(
                        description = "How much?",
                        variants = listOf(
                            AnswerVariant("One", false),
                            AnswerVariant("Two", true)
                        )
                    ),
                    Question(
                        description = "How are you",
                        variants = listOf(AnswerVariant("good", true))
                    )
                )
            ),
            Quiz(
                id = 2,
                authorName = "upconett",
                name = "Nice quiz",
                description = "",
                questions = listOf(
                    Question(
                        description = "Hate me?",
                        variants = listOf(AnswerVariant("No", true))
                    )
                )
            )
        )
}