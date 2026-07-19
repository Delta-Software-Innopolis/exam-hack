package com.examhacker.domain.model

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)
