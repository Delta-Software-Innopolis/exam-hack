package com.examhacker.domain.model

data class Question(
    val id: Int,
    val description: String,
    val variants: List<AnswerVariant>
)

data class AnswerVariant(
    val description: String,
    val isCorrect: Boolean
)