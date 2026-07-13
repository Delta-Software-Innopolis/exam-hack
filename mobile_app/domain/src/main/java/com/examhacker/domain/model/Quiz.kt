package com.examhacker.domain.model

data class Quiz(
    val info: QuizInfo,
    val description: String,
    val questions: List<Question>
)