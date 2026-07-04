package com.examhacker.common.data

data class Question(
    val description: String,
    val variants: List<AnswerVariant>
)

data class AnswerVariant(
    val description: String,
    val isCorrect: Boolean
)