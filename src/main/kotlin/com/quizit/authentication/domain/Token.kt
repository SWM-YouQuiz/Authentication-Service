package com.quizit.authentication.domain

data class Token(
    val userId: String,
    val content: String
)