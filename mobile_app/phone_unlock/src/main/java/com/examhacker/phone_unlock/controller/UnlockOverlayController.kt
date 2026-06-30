package com.examhacker.phone_unlock.controller

import com.examhacker.common.utility.AnswerVariant
import com.examhacker.common.utility.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UnlockOverlayController(
//    private val repository: QuizRepository,
    private val dismissOverlay: () -> Unit,
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
        val question = _state.value.question ?: return

        _state.update {
            it.copy(finalAnswer = answer)
        }

//        dismissOverlay()
    }

    fun takeHint() {
        // TODO
    }

    fun proceed() {
        dismissOverlay()
    }

    fun reset() {
        _state.update { State() }
        loadMockQuestions()
//        loadQuestion()
    }

    fun loadMockQuestions() {
        _state.update {
            it.copy(
                question = Question(
                    description = "Question description, may span several lines, we’ll discuss the font size and boldness later",
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