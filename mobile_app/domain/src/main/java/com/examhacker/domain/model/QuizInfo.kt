package com.examhacker.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class QuizInfo(
    val id: Int,
    val name: String,
    val creationDate: String,
    val updatingDate: String? = null,
    val author: Author
)

@Serializable
data class Author(
    val id: Int,
    val name: String
)