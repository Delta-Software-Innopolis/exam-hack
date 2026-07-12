package com.examhacker.data_network.repository

import com.examhacker.data_network.dto.Card
import com.examhacker.data_network.dto.CardUpdate
import com.examhacker.data_network.dto.CardsCreateRequest
import com.examhacker.data_network.dto.CardsResponse
import com.examhacker.data_network.dto.CardsUpdateRequest
import com.examhacker.data_network.dto.Pack
import com.examhacker.data_network.dto.PackCreateRequest
import com.examhacker.data_network.dto.PackUpdateRequest
import com.examhacker.data_network.dto.PackWithCards
import com.examhacker.data_network.dto.PacksResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface QuizRepository {
    suspend fun createPack(name: String): Result<Pack>
    suspend fun updatePack(packId: Int, newName: String): Result<Pack>
    suspend fun deletePack(packId: Int): Result<Unit>
    suspend fun getAllPacks(): Result<List<PackWithCards>>
    suspend fun createCards(packId: Int, cards: List<Card>): Result<List<Card>>
    suspend fun updateCards(cards: List<CardUpdate>): Result<List<Card>>
    suspend fun deleteCard(cardId: Int): Result<Unit>
    suspend fun getCardsForPack(packId: Int): Result<List<Card>>
}

class QuizRepositoryImpl(private val client: HttpClient) : QuizRepository {

    // Pack operations
    override suspend fun createPack(name: String): Result<Pack> {
        return try {
            val response = client.post("/core/pack") {
                contentType(ContentType.Application.Json)
                setBody(PackCreateRequest(name))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePack(packId: Int, newName: String): Result<Pack> {
        return try {
            val response = client.patch("/core/pack/$packId") {
                contentType(ContentType.Application.Json)
                setBody(PackUpdateRequest(newName))
            }
            Result.success(response.body())
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

    override suspend fun getAllPacks(): Result<List<PackWithCards>> {
        return try {
            val response = client.get("/core/packs")
            val packsResponse = response.body<PacksResponse>()
            Result.success(packsResponse.packs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Card operations
    override suspend fun createCards(packId: Int, cards: List<Card>): Result<List<Card>> {
        return try {
            val response = client.post("/core/cards/$packId") {
                contentType(ContentType.Application.Json)
                setBody(CardsCreateRequest(cards))
            }
            val cardsResponse = response.body<CardsResponse>()
            Result.success(cardsResponse.cards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCards(cards: List<CardUpdate>): Result<List<Card>> {
        return try {
            val response = client.patch("/core/cards") {
                contentType(ContentType.Application.Json)
                setBody(CardsUpdateRequest(cards))
            }
            val cardsResponse = response.body<CardsResponse>()
            Result.success(cardsResponse.cards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCard(cardId: Int): Result<Unit> {
        return try {
            client.delete("/core/cards/$cardId")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCardsForPack(packId: Int): Result<List<Card>> {
        return try {
            val response = client.get("/core/cards/$packId")
            val cardsResponse = response.body<CardsResponse>()
            Result.success(cardsResponse.cards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}