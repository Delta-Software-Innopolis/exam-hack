package com.examhacker.data_network.dto

data class AuthRequest(
    val username: String,
    val password: String
)

data class RefreshRequest(
    val refresh_token: String
)

data class AuthResponse(
    val access_token: String,
    val refresh_token: String
)