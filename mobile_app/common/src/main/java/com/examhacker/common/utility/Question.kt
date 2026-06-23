package com.examhacker.common.utility

data class Question(
    val description: String,
    val variants: List<AnswerVariant>
)

data class AnswerVariant(
    val description: String,
    val isCorrect: Boolean
)