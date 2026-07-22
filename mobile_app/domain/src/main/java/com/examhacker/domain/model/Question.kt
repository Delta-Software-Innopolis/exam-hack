package com.examhacker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int,
    val description: String,
    val hint: String? = null,
    val variants: List<AnswerVariant>
)

@Serializable
data class AnswerVariant(
    val description: String,
    val isCorrect: Boolean
)