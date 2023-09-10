package com.quizit.authentication.dto.response

import com.quizit.authentication.domain.enum.Role
import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val username: String,
    val nickname: String,
    val image: String?,
    val role: Role,
    val allowPush: Boolean,
    val dailyTarget: Int,
    val answerRate: Double,
    val createdDate: LocalDateTime,
    val correctQuizIds: Set<String>,
    val incorrectQuizIds: Set<String>,
    val markedQuizIds: Set<String>
)