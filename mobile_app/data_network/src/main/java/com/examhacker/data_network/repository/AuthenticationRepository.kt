package com.examhacker.data_network.repository

import android.util.Log
import com.examhacker.data_network.dto.AuthRequest
import com.examhacker.data_network.dto.LogoutRequest
import com.examhacker.data_network.dto.NetworkAuthResponse
import com.examhacker.data_network.dto.RefreshRequest
import com.examhacker.data_network.dto.toDomain
import com.examhacker.domain.model.AuthResponse
import com.examhacker.domain.repository.IAuthenticationRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthenticationRepository(private val client: HttpClient) : IAuthenticationRepository {

    override suspend fun register(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("/auth/reg") {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(username, password))
            }
            Result.success(response.body<NetworkAuthResponse>().toDomain())
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
            Result.success(response.body<NetworkAuthResponse>().toDomain())
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
            Result.success(response.body<NetworkAuthResponse>().toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(refreshToken: String): Result<Unit> {
        return try {
            val response = client.post("/auth/logout") {
                contentType(ContentType.Application.Json)
                setBody(LogoutRequest(refreshToken))
            }

            Log.d("LogoutDebug", "Response: $response")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}