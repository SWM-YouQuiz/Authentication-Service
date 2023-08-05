package com.youquiz.authentication.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)