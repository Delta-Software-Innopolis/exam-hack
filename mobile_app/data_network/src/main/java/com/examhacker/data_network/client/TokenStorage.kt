package com.examhacker.data_network.client

import android.content.Context
import android.content.SharedPreferences
import com.examhacker.domain.repository.ITokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenStoragePrefs(private val context: Context) : ITokenStorage {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    override suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            prefs.getString(ACCESS_TOKEN_KEY, null)
        }
    }

    override suspend fun getRefreshToken(): String? {
        return withContext(Dispatchers.IO) {
            prefs.getString(REFRESH_TOKEN_KEY, null)
        }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        withContext(Dispatchers.IO) {
            prefs.edit().apply {
                putString(ACCESS_TOKEN_KEY, accessToken)
                putString(REFRESH_TOKEN_KEY, refreshToken)
                apply()
            }
        }
    }

    override suspend fun clearTokens() {
        withContext(Dispatchers.IO) {
            prefs.edit().apply {
                remove(ACCESS_TOKEN_KEY)
                remove(REFRESH_TOKEN_KEY)
                apply()
            }
        }
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}
