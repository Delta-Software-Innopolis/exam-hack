package com.examhacker.domain.model

data class QuestionUpdate(
    val id: Int,
    val question: String? = null,
    val hint: String? = null,
    val options: List<String>? = null,
    val correct: List<Int>? = null
)