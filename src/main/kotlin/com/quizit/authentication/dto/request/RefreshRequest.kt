package com.quizit.authentication.dto.request

data class RefreshRequest(
    val userId: String,
    val refreshToken: String
)