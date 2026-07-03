package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext

interface IQuizResultComponent {
    fun continueGrinding()
    fun takeBreak()
    fun back()
    val uiState: QuizResultUiState
}

data class QuizResultUiState(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val progressDelta: String,
    val examTime: String
)

class QuizResultComponent(
    componentContext: ComponentContext,
    private val onContinueGrinding: () -> Unit,
    private val onTakeBreak: () -> Unit,
    private val goBack: () -> Unit
) : IQuizResultComponent,
    ComponentContext by componentContext {

    override val uiState = QuizResultUiState(
        correctAnswers = 4,
        totalQuestions = 5,
        progressDelta = "+0.8%",
        examTime = "2d:3h:20m"
    )
    // TODO Replace with real quiz results

    override fun continueGrinding() {
        onContinueGrinding()
    }

    override fun takeBreak() {
        onTakeBreak()
    }

    override fun back() {
        goBack()
    }
}