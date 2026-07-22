package com.examhacker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class QuestionGenerated(
    val description: String,
    val hint: String? = null,
    val variants: List<AnswerVariant>
)