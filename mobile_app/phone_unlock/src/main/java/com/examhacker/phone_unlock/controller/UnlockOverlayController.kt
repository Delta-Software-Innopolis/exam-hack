package com.examhacker.phone_unlock.controller

import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Question
import com.examhacker.domain.model.Quiz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UnlockOverlayController(
//    private val repository: QuizRepository,
    private val dismissOverlay: () -> Unit,
    private val updateQuestionAnsweredStatus: (Int) -> Unit,
    private val scope: CoroutineScope
) {
    data class State(
        val question: Question? = null,
        val finalAnswer: AnswerVariant? = null,
        val solvedQuestions: Int = 1,
        val totalQuestions: Int = 5,
        val loading: Boolean = true
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
//        loadQuestion()
        loadMockQuestions()
    }

//    private fun loadQuestion() {
//        scope.launch {
//
//            val question = repository.getRandomQuestion()
//
//            _state.update {
//                it.copy(
//                    question = question,
//                    finalAnswer = null,
//                    loading = false
//                )
//            }
//        }
//    }

    fun submitAnswer(answer: AnswerVariant) {
        _state.value.question?.let {
            _state.update {
                it.copy(finalAnswer = answer)
            }
        }
    }

    fun takeHint() {
        // TODO
    }

    fun proceed() {
        state.value.question?.let {
            updateQuestionAnsweredStatus(it.id)
        }
        dismissOverlay()
    }

    fun reset() {
        _state.update { State() }
//        loadMockQuestions()
//        loadQuestion()
    }

    fun setQuestion(quiz: Quiz, answered: List<Boolean>) {
        val question = quiz.questions
            .filterIndexed { index, _ -> !answered[index] }
            .firstOrNull()

        _state.update { state ->
            state.copy(
                question = question,
                totalQuestions = quiz.questions.size,
                solvedQuestions =
                    if (answered.count { !it } == 0)
                        0
                    else
                        answered.count { it }
            )
        }
    }

    fun loadMockQuestions() {
        _state.update {
            it.copy(
                question = Question(
                    id = 0,
                    description = "Question description, may span several lines, we’ll discuss the font size and boldness later",
                    hint = "",
                    variants = listOf(
                        AnswerVariant(
                            description = "Option 1, option description",
                            isCorrect = false
                        ),
                        AnswerVariant(
                            description = "Option 2, description, maybe a correct answer",
                            isCorrect = false
                        ),
                        AnswerVariant(
                            description = "Option 3, description, choose wisely",
                            isCorrect = false
                        ),
                        AnswerVariant(
                            description = "Option 4, idk which is correct, really",
                            isCorrect = true
                        )
                    )
                )
            )
        }
    }
}