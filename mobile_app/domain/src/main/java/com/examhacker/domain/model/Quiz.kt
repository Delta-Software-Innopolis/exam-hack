package com.examhacker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val info: QuizInfo,
    val description: String,
    val questions: List<Question>
)