package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

interface IQuizResultComponent {

    val model: Value<Model>
    data class Model(
        val correctAnswers: Int = 0,
        val totalQuestions: Int = 0
    )

    fun quitSolving()
    fun back()
}

class QuizResultComponent(
    componentContext: ComponentContext,
    correctAnswerCount: Int,
    questionCount: Int,
    private val onQuitSolving: () -> Unit,
    private val goBack: () -> Unit
) : IQuizResultComponent, ComponentContext by componentContext {
    
    private val _model = MutableValue(IQuizResultComponent.Model())
    override val model = _model

    init {
        _model.update {
            it.copy(
                correctAnswers = correctAnswerCount,
                totalQuestions = questionCount
            )
        }
    }

    override fun quitSolving() {
        onQuitSolving()
    }

    override fun back() {
        goBack()
    }
}