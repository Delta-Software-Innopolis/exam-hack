package com.examhacker.domain.repository

import com.examhacker.domain.model.Question
import com.examhacker.domain.model.QuestionUpdate
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.model.QuizInfo

interface IQuizRepository {
    suspend fun createPack(name: String): Result<QuizInfo>
    suspend fun updatePack(packId: Int, newName: String): Result<QuizInfo>
    suspend fun deletePack(packId: Int): Result<Unit>
    suspend fun getAllPacks(): Result<List<Quiz>>
    suspend fun createCards(packId: Int, questions: List<Question>): Result<List<Question>>
    suspend fun updateCards(questions: List<QuestionUpdate>): Result<List<Question>>
    suspend fun deleteCard(cardId: Int): Result<Unit>
    suspend fun getCardsForPack(packId: Int): Result<List<Question>>
}