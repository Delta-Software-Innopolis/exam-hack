package com.examhacker.data_network.dto

import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Author
import com.examhacker.domain.model.Question
import com.examhacker.domain.model.QuestionCreate
import com.examhacker.domain.model.QuestionUpdate
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.model.QuizInfo
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal fun Pack.toDomain(): QuizInfo =
    QuizInfo(
        id = this.id,
        name = this.name,
        creationDate = this.creation_date,
        updatingDate = this.updating_date,
        author = Author(
            id = this.author.id,
            name = this.author.name
        )
    )

@OptIn(ExperimentalTime::class)
internal fun PackWithCards.toDomain(): Quiz =
    Quiz(
        info = QuizInfo(
            id = this.id,
            name = this.name,
            creationDate = this.creation_date,
            updatingDate = this.updating_date,
            author = Author(
                id = this.author.id,
                name = this.author.name
            )
        ),
        description = this.description ?: "",
        questions = this.cards.map { it.toDomain() }
    )

internal fun Card.toDomain(): Question =
    Question(
        id = this.id,
        description = this.question,
        hint = this.hint ?: "Hint text",
        variants = List(this.options.size) { index ->
            AnswerVariant(
                description = this.options[index],
                isCorrect = this.correct.contains(index)
            )
        }
    )

internal fun Question.toNetwork(): Card =
    Card(
        id = this.id,
        question = this.description,
        hint = this.hint,
        options = this.variants.map { it.description },
        correct = this.variants
            .mapIndexed { index, variant ->
                if (variant.isCorrect) index else null
            }
            .filterNotNull()
    )

internal fun QuestionUpdate.toNetwork(): CardUpdate =
    CardUpdate(
        id = this.id,
        question = this.question,
        hint = this.hint,
        options = this.options,
        correct = this.correct
    )

internal fun QuestionCreate.toNetwork(): CardCreate =
    CardCreate(
        question = this.description,
        hint = this.hint,
        options = this.variants.map { it.description },
        correct = this.variants
            .mapIndexed { index, variant ->
                if (variant.isCorrect) index else null
            }
            .filterNotNull()
    )