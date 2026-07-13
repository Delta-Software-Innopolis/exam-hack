package com.examhacker.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class QuizInfo(
    val id: Int,
    val name: String,
    val creationDate: Instant,
    val updatingDate: Instant? = null,
    val author: Author
)

data class Author(
    val id: Int,
    val name: String
)