package com.examhacker.quiz_list.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.repository.IQuizRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

interface IQuizListComponent {

    val model: Value<Model>

    data class Model(
        val quizzes: List<Quiz>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    fun onAddQuiz()
    fun onQuizClick(quizId: Int)
    fun goToQuizHub()
    fun goToProfile()
    fun goToSettings()
    fun back()
}

class QuizListComponent(
    componentContext: ComponentContext,
    private val quizStateFlow: Flow<List<Quiz>?>,
    private val quizRepository: IQuizRepository,
    private val saveQuizzes: (List<Quiz>) -> Unit,
    private val toQuizInfo: (Int) -> Unit,
    private val toQuizCreate: () -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val goBack: () -> Unit
) : IQuizListComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizListComponent.Model())
    override val model = _model

    init {
        loadQuizzes()

        CoroutineScope(Dispatchers.IO).launch {
            quizStateFlow.collect { quizzes ->
                quizzes?.let {
                    _model.update {
                        it.copy(quizzes = quizzes)
                    }
                }
            }
        }
    }

    override fun onAddQuiz() {
        toQuizCreate()
    }

    override fun onQuizClick(quizId: Int) {
        toQuizInfo(quizId)
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

    private fun loadQuizzes() {
        CoroutineScope(Dispatchers.IO).launch {
            _model.update {
                it.copy(isLoading = true)
            }

            quizRepository.getAllPacks()
                .onSuccess { quizzes ->
                    _model.update {
                        it.copy(
                            quizzes = quizzes,
                            isLoading = false
                        )
                    }

                    saveQuizzes(quizzes)
                }
                .onFailure { exception ->
                    _model.update {
                        it.copy(
                            error = exception.message,
                            isLoading = false
                        )
                    }
                }
        }
    }
}