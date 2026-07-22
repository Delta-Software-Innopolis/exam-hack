package com.examhacker.domain.repository

import com.examhacker.domain.model.AuthResponse

interface IAuthenticationRepository {
    suspend fun register(username: String, password: String): Result<AuthResponse>
    suspend fun login(username: String, password: String): Result<AuthResponse>
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse>
    suspend fun logout(refreshToken: String): Result<Unit>
}