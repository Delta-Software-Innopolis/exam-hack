package com.examhacker.common.data

data class Quiz(
    val name: String,
    val description: String,
    val questions: List<Question>
)