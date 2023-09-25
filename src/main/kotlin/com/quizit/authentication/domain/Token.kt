package com.quizit.authentication.domain

import org.springframework.data.redis.core.RedisHash

@RedisHash
class Token(
    val userId: String,
    val content: String
)