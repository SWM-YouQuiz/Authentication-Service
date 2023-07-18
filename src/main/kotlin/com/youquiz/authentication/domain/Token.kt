package com.youquiz.authentication.domain

data class Token(
    val userId: Long,
    val content: String
)