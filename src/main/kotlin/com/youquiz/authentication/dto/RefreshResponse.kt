package com.youquiz.authentication.dto

data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String
)