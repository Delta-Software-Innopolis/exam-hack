package com.examhacker.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val description: String,
    val variants: List<AnswerVariant>
)

@Serializable
data class AnswerVariant(
    val description: String,
    val isCorrect: Boolean
)