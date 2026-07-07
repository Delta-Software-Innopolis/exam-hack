package com.examhacker.quiz_info.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.data.Quiz
import com.examhacker.common.data.QuizStatistics

interface IQuizInfoComponent {
    val model: Value<Model>

    data class Model(
        val quiz: Quiz = Quiz(0, "", "", "", emptyList()),
        val statistics: QuizStatistics = QuizStatistics(0, 0.0f, 0, 0.0f)
    )

    fun attemptQuiz()
    fun viewQuestions()
    fun onDeleteQuiz()
    fun goBack()
}

class QuizInfoComponent(
    componentContext: ComponentContext,
    quiz: Quiz,
    private val toSolve: () -> Unit,
    private val toEdit: () -> Unit,
    private val deleteQuiz: () -> Unit,
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