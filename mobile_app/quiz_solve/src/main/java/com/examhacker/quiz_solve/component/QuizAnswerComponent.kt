package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext

interface IQuizAnswerComponent {
    fun back()
    fun previousQuestion()
    fun nextQuestion()
    fun openAiChat()
    val uiState: QuizAnswerUiState
}

enum class AnswerState {
    DEFAULT,
    SELECTED,
    CORRECT,
    INCORRECT
}

data class AnswerItem(
    val text: String,
    val state: AnswerState = AnswerState.DEFAULT
)

data class QuizAnswerUiState(
    val currentQuestion: Int,
    val totalQuestions: Int,
    val question: String,
    val answers: List<AnswerItem>
)

class QuizAnswerComponent(
    componentContext: ComponentContext,
    private val goBack: () -> Unit,
    private val onPreviousQuestion: () -> Unit,
    private val onNextQuestion: () -> Unit,
    private val onOpenAiChat: () -> Unit
) : IQuizAnswerComponent,
    ComponentContext by componentContext {

    override val uiState = QuizAnswerUiState(
        currentQuestion = 1,
        totalQuestions = 5,
        question = "Question description, may span several lines, we'll discuss the font size and boldness later",
        answers = listOf(
            AnswerItem("Option 1, option description"),
            AnswerItem("Option 2, description, maybe a correct answer"),
            AnswerItem("Option 3, description, choose wisely"),
            AnswerItem("Option 4, idk which is correct, really")
        )
    )
    // TODO Replace with real quiz data

    override fun previousQuestion() {
        onPreviousQuestion()
    }

    override fun nextQuestion() {
        onNextQuestion()
    }

    override fun openAiChat() {
        onOpenAiChat()
    }

    override fun back() {
        goBack()
    }
}