package com.examhacker.domain.model

data class QuestionCreate(
    val description: String,
    val hint: String,
    val variants: List<AnswerVariant>
)
