package com.examhacker.data_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class Pack(
    val id: Int,
    val name: String,
    val creation_date: String,
    val updating_date: String? = null,
    val author: PackAuthor
)

@Serializable
data class PackCreateRequest(
    val name: String
)

@Serializable
data class PackUpdateRequest(
    val name: String
)

@Serializable
data class PacksResponse(
    val packs: List<PackWithCards>
)

@Serializable
data class PackWithCards(
    val id: Int,
    val name: String,
    val description: String? = null,
    val creation_date: String,
    val updating_date: String? = null,
    val author: PackAuthor,
    val cards: List<Card>
)

@Serializable
data class PackAuthor(
    val id: Int,
    val name: String
)

@Serializable
data class Card(
    val id: Int,
    val question: String,
    val hint: String? = null,
    val options: List<String>,
    val correct: List<Int>
)

@Serializable
data class CardsCreateRequest(
    val cards: List<Card>
)

@Serializable
data class CardsUpdateRequest(
    val cards: List<CardUpdate>
)

@Serializable
data class CardUpdate(
    val id: Int,
    val question: String? = null,
    val hint: String? = null,
    val options: List<String>? = null,
    val correct: List<Int>? = null
)

@Serializable
data class CardsResponse(
    val cards: List<Card>
)