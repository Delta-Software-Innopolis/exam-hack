package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import kotlin.compareTo

interface IQuizQuestionComponent {
    val model: Value<Model>

    data class Model(
        val questions: List<Question> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val answers: List<AnswerVariant?> = emptyList(),
        val nextEnabled: Boolean = false,
        val previousEnabled: Boolean = false
    )

    fun back()
    fun goToPreviousQuestion()
    fun goToNextQuestion()
    fun answerCurrentQuestion(answer: AnswerVariant)
    fun openAiChat()
}

class QuizQuestionComponent(
    componentContext: ComponentContext,
    private val goBack: () -> Unit,
    private val onOpenAiChat: () -> Unit
) : IQuizQuestionComponent, ComponentContext by componentContext {

    private val _model = MutableValue(createMockData())//MutableValue(IQuizQuestionComponent.Model())
    override val model = _model

    override fun goToPreviousQuestion() {
        if (model.value.previousEnabled) {
            _model.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex - 1,
                    nextEnabled = isNextEnabled(it.currentQuestionIndex - 1),
                    previousEnabled = isPreviousEnabled(it.currentQuestionIndex - 1)
                )
            }
        }
    }

    override fun goToNextQuestion() {
        if (model.value.nextEnabled) {
            _model.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    nextEnabled = isNextEnabled(it.currentQuestionIndex + 1),
                    previousEnabled = isPreviousEnabled(it.currentQuestionIndex + 1)
                )
            }
        }
    }

    override fun answerCurrentQuestion(answer: AnswerVariant) {
        val updatedAnswers = model.value.answers.toMutableList()
        updatedAnswers.add(model.value.currentQuestionIndex, answer)

        _model.update {
            it.copy(answers = updatedAnswers)
        }
    }

    override fun openAiChat() {
        onOpenAiChat()
    }

    override fun back() {
        goBack()
    }

    private fun isNextEnabled(index: Int): Boolean {
        return index < model.value.questions.lastIndex
            && index != -1
    }

    private fun isPreviousEnabled(index: Int): Boolean {
        return index > 0
    }
    
    private fun createMockData(): IQuizQuestionComponent.Model {
        val question1 = Question(
            description = "Question description, may span several lines, we’ll discuss the font size and boldness later",
            variants = listOf(
                AnswerVariant("Option 1, option description", false),
                AnswerVariant("Option 2, description, maybe a correct answer", false),
                AnswerVariant("Option 3, description, choose wisely", false),
                AnswerVariant("Option 4, idk which is correct, really", true)
            )
        )
        
        val question2 = Question(
            description = "Why do dogs think their tails are so clingy, they always want to grab it?",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", false),
                AnswerVariant("Option 3", true),
                AnswerVariant("Option 4", false)
            )
        )

        return IQuizQuestionComponent.Model(
            questions = listOf(question1, question2),
            currentQuestionIndex = 0,
            answers = emptyList(),
            nextEnabled = true,
            previousEnabled = false
        )
    }
}