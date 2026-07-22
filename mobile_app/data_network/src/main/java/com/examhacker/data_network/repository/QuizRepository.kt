package com.examhacker.data_network.repository

import android.util.Log
import com.examhacker.data_network.dto.CardsCreateRequest
import com.examhacker.data_network.dto.CardsDeleteRequest
import com.examhacker.data_network.dto.CardsGenerateResponse
import com.examhacker.data_network.dto.CardsResponse
import com.examhacker.data_network.dto.CardsUpdateRequest
import com.examhacker.data_network.dto.CardsUpdateResponse
import com.examhacker.data_network.dto.Pack
import com.examhacker.data_network.dto.PackCreateRequest
import com.examhacker.data_network.dto.PackUpdateRequest
import com.examhacker.data_network.dto.PacksResponse
import com.examhacker.data_network.dto.toDomain
import com.examhacker.data_network.dto.toNetwork
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Question
import com.examhacker.domain.model.QuestionCreate
import com.examhacker.domain.model.QuestionGenerated
import com.examhacker.domain.model.QuestionUpdate
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.model.QuizInfo
import com.examhacker.domain.repository.IQuizRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import java.io.File

class QuizRepository(private val client: HttpClient) : IQuizRepository {

    // Pack operations
    override suspend fun createPack(name: String, description: String?): Result<QuizInfo> {
        return try {
            val response = client.post("/core/pack") {
                contentType(ContentType.Application.Json)
                setBody(PackCreateRequest(name, description))
            }
            Log.d("CreateDebugRepo", "Response: $response")
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

    override suspend fun generateCards(
        name: String,
        count: Int,
        files: List<File>
    ): Result<List<QuestionGenerated>> {
        return try {
            val multipartBody = MultiPartFormDataContent(
                formData {
                    append("name", name)
                    append("card_type", "multiple_choice")
                    append("count", count.toString())

                    files.forEach { file ->
                        append(
                            key = "files",
                            value = file.readBytes(),
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"files\"; filename=\"${file.name}\""
                                )
                                append(
                                    HttpHeaders.ContentType,
                                    "application/pdf"
                                )
                            }
                        )
                    }
                }
            )

            Log.d("CreateDebugRepo", "Generating cards for pack: $name, count: $count, files: ${files.size}")

            val response = client.post("/core/cards/generate") {
                contentType(ContentType.MultiPart.FormData)
                setBody(multipartBody)
            }

            if (response.status != HttpStatusCode.OK) {
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Unknown error"
                }
                Log.e("CreateDebugRepo", "Generation failed with status: ${response.status}, body: $errorBody")
                return Result.failure(Exception("Generation failed: $errorBody"))
            }

            val generateResponse = try {
                response.body<CardsGenerateResponse>()
            } catch (e: Exception) {
                val errorBody = response.body<String>()
                Log.e("CreateDebugRepo", "Failed to parse response: $errorBody")
                throw Exception("Failed to parse server response: ${e.message}")
            }

            val questions = generateResponse.cards.map { card ->
                QuestionGenerated(
                    description = card.question,
                    hint = card.hint,
                    variants = card.options.mapIndexed { index, option ->
                        AnswerVariant(
                            description = option,
                            isCorrect = card.correct_indices?.contains(index) ?: false
                        )
                    }
                )
            }

            Log.d("CreateDebugRepo", "Successfully generated ${questions.size} cards")
            Result.success(questions)

        } catch (e: Exception) {
            Log.e("CreateDebugRepo", "Generate cards failed", e)
            Result.failure(e)
        }
    }

}