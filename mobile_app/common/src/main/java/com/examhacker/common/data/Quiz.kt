package com.examhacker.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val id: Int,
    val authorName: String,
    val name: String,
    val description: String,
    val questions: List<Question>
)