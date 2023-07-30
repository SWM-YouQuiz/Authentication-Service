package com.youquiz.authentication.dto

data class RefreshRequest(
    val userId: String,
    val refreshToken: String
)