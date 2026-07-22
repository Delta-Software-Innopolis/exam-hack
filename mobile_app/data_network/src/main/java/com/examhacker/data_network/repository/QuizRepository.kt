package com.examhacker.data_network.repository

import android.util.Log
import com.examhacker.data_network.dto.CardsCreateRequest
import com.examhacker.data_network.dto.CardsDeleteRequest
import com.examhacker.data_network.dto.CardsResponse
import com.examhacker.data_network.dto.CardsUpdateRequest
import com.examhacker.data_network.dto.CardsUpdateResponse
import com.examhacker.data_network.dto.Pack
import com.examhacker.data_network.dto.PackCreateRequest
import com.examhacker.data_network.dto.PackUpdateRequest
import com.examhacker.data_network.dto.PacksResponse
import com.examhacker.data_network.dto.toDomain
import com.examhacker.data_network.dto.toNetwork
import com.examhacker.domain.model.Question
import com.examhacker.domain.model.QuestionCreate
import com.examhacker.domain.model.QuestionUpdate
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.model.QuizInfo
import com.examhacker.domain.repository.IQuizRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class QuizRepository(private val client: HttpClient) : IQuizRepository {

    // Pack operations
    override suspend fun createPack(name: String): Result<QuizInfo> {
        return try {
            val response = client.post("/core/pack") {
                contentType(ContentType.Application.Json)
                setBody(PackCreateRequest(name))
            }
            Result.success(response.body<Pack>().toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePack(packId: Int, newName: String): Result<QuizInfo> {
        return try {
            val response = client.patch("/core/pack/$packId") {
                contentType(ContentType.Application.Json)
                setBody(PackUpdateRequest(newName))
            }
            Result.success(response.body<Pack>().toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePack(packId: Int): Result<Unit> {
        return try {
            client.delete("/core/pack/$packId")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllPacks(): Result<List<Quiz>> {
        return try {
            val response = client.get("/core/packs")
            val packsResponse = response.body<PacksResponse>()
            Result.success(packsResponse.packs.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Card operations
    override suspend fun createCards(packId: Int, questions: List<QuestionCreate>): Result<List<Question>> {
        return try {
            val cards = questions.map { it.toNetwork() }
            val request = CardsCreateRequest(cards)

            val response = client.post("/core/cards/$packId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val cardsResponse = response.body<CardsResponse>()
            Result.success(cardsResponse.cards.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCards(questions: List<QuestionUpdate>): Result<List<Question>> {
        return try {
            val updates = questions.map { it.toNetwork() }
            val request = CardsUpdateRequest(cards = updates)

            val response = client.patch("/core/cards") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(request))
            }

            val updateResponse = response.body<CardsUpdateResponse>()
            Result.success(updateResponse.cards.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCards(cardIds: List<Int>): Result<Unit> {
        return try {
            val request = CardsDeleteRequest(cardIds)

            client.delete("/core/cards") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCardsForPack(packId: Int): Result<List<Question>> {
        return try {
            val response = client.get("/core/cards/$packId")
            val cardsResponse = response.body<CardsResponse>()
            Result.success(cardsResponse.cards.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}