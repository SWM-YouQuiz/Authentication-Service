package com.youquiz.authentication.domain

data class Token(
    val userId: String,
    val content: String
)