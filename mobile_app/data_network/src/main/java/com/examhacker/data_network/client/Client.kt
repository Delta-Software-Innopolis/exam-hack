package com.examhacker.data_network.client

import com.examhacker.data_network.dto.AuthResponse
import com.examhacker.data_network.dto.RefreshRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Client {
    private const val BASE_URL = "https://your-api-domain.com" // Replace with your actual domain

    fun provideHttpClient(
        tokenStorage: TokenStorage
    ): HttpClient {
        return HttpClient(CIO) {

            engine {
                endpoint {
                    connectTimeout = 30_000L
                    connectAttempts = 3
                }
                requestTimeout = 60_000L
            }

            defaultRequest {
                url(BASE_URL)
                contentType(Json)
                header("Accept", "application/json")
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }

            install(Logging) {
                level = LogLevel.HEADERS
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        android.util.Log.d("KtorClient", message)
                    }
                }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = tokenStorage.getAccessToken()
                        val refreshToken = tokenStorage.getRefreshToken()
                        if (accessToken != null && refreshToken != null) {
                            BearerTokens(accessToken, refreshToken)
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        val refreshToken = tokenStorage.getRefreshToken()
                        if (refreshToken != null) {
                            try {
                                val response = client.post("/auth/refresh") {
                                    contentType(Json)
                                    setBody(RefreshRequest(refreshToken))
                                }
                                val authResponse = response.body<AuthResponse>()
                                tokenStorage.saveTokens(
                                    authResponse.access_token,
                                    authResponse.refresh_token
                                )
                                BearerTokens(
                                    authResponse.access_token,
                                    authResponse.refresh_token
                                )
                            } catch (e: Exception) {
                                tokenStorage.clearTokens()
                                null
                            }
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }
}