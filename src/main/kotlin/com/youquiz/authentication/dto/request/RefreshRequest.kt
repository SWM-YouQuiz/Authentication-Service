package com.youquiz.authentication.dto.request

data class RefreshRequest(
    val userId: String,
    val refreshToken: String
)