package com.examhacker.quiz_info.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.model.QuizStatistics

interface IQuizInfoComponent {
    val model: Value<Model>

    data class Model(
        val quiz: Quiz? = null,
        val statistics: QuizStatistics = QuizStatistics(0, 0.0f, 0, 0.0f)
    )

    fun attemptQuiz()
    fun viewQuestions()
    fun onDeleteQuiz()
    fun goToQuizHub()
    fun goToProfile()
    fun goToSettings()
    fun goBack()
}

class QuizInfoComponent(
    componentContext: ComponentContext,
    quiz: Quiz,
    private val toSolve: () -> Unit,
    private val toEdit: () -> Unit,
    private val deleteQuiz: () -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val back: () -> Unit
) : IQuizInfoComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizInfoComponent.Model())
    override val model = _model

    init {
        _model.update {
            it.copy(
                quiz = quiz,
                statistics = createMockStatistics()
            )
        }
    }

    override fun attemptQuiz() {
        toSolve()
    }

    override fun viewQuestions() {
        toEdit()
    }

    override fun onDeleteQuiz() {
        deleteQuiz()
    }

    override fun goToQuizHub() {
        toQuizHub()
    }

    override fun goToProfile() {
        toProfile()
    }

    override fun goToSettings() {
        toSettings()
    }

    override fun goBack() {
        back()
    }

    private fun createMockStatistics() =
        QuizStatistics(
            quizId = 1,
            progress = 0.32f,
            attemptsNumber = 67,
            rightWrongRatio = 0.76f
        )
}