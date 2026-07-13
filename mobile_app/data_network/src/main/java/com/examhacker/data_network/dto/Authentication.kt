package com.examhacker.data_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)

@Serializable
data class RefreshRequest(
    val refresh_token: String
)

@Serializable
data class NetworkAuthResponse(
    val access_token: String,
    val refresh_token: String
)