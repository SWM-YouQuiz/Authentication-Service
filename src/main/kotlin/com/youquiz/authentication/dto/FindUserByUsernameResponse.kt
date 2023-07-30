package com.youquiz.authentication.dto

import java.time.LocalDateTime

data class FindUserByUsernameResponse(
    val id: String,
    val username: String,
    val nickname: String,
    val role: String,
    val allowPush: Boolean,
    val createdDate: LocalDateTime,
    val correctQuizIds: Set<String>,
    val incorrectQuizIds: Set<String>,
    val likedQuizIds: Set<String>
)