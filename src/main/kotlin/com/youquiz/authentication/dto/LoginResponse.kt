package com.youquiz.authentication.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)