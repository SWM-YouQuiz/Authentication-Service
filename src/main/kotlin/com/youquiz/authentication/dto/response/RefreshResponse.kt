package com.youquiz.authentication.dto.response

data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String
)