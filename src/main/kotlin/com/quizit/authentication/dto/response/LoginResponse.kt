package com.quizit.authentication.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)