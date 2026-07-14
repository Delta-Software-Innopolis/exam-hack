package com.examhacker.quiz_list.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.data.Quiz

interface IQuizListComponent {

    val model: Value<Model>

    data class Model(
        val quizzes: List<Quiz>? = null
    )

    fun onAddQuiz()
    fun onQuizClick()
    fun goToQuizHub()
    fun goToProfile()
    fun goToSettings()
    fun back()
}

class QuizListComponent(
    componentContext: ComponentContext,
    quizzes: List<Quiz>,
    private val toQuizInfo: () -> Unit,
    private val toQuizCreate: () -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val goBack: () -> Unit
) : IQuizListComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizListComponent.Model())
    override val model = _model

    init {
        _model.update {
            it.copy(quizzes = quizzes)
        }
    }

    override fun onAddQuiz() {
        toQuizCreate()
    }

    override fun onQuizClick() {
        toQuizInfo()
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

    override fun back() {
        goBack()
    }
}