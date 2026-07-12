package com.examhacker.data_network.repository

import com.examhacker.data_network.dto.AuthRequest
import com.examhacker.data_network.dto.AuthResponse
import com.examhacker.data_network.dto.RefreshRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface AuthRepository {
    suspend fun register(username: String, password: String): Result<AuthResponse>
    suspend fun login(username: String, password: String): Result<AuthResponse>
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse>
    suspend fun logout(): Result<Unit>
}

class AuthRepositoryImpl(private val client: HttpClient) : AuthRepository {

    override suspend fun register(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("/auth/reg") {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(username, password))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(username, password))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            val response = client.post("/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequest(refreshToken))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            client.post("/auth/logout")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}