package com.quizit.authentication.domain

import org.springframework.data.redis.core.RedisHash

@RedisHash
data class RefreshToken(
    val userId: String,
    val content: String
)