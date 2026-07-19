package com.examhacker.data_network.dto

import com.examhacker.domain.model.AuthResponse

internal fun NetworkAuthResponse.toDomain(): AuthResponse =
    AuthResponse(
        accessToken = this.access_token,
        refreshToken = this.refresh_token
    )