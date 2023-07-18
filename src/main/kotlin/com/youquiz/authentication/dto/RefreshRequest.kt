package com.youquiz.authentication.dto

data class RefreshRequest(
    val userId: Long,
    val refreshToken: String
)