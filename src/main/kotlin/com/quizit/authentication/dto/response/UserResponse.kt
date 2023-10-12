package com.quizit.authentication.dto.response

import com.quizit.authentication.domain.enum.Provider
import com.quizit.authentication.domain.enum.Role
import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val username: String,
    val nickname: String,
    val image: String?,
    val level: Int,
    val role: Role,
    val allowPush: Boolean,
    val dailyTarget: Int,
    val answerRate: Double,
    val provider: Provider?,
    val correctQuizIds: HashSet<String>,
    val incorrectQuizIds: HashSet<String>,
    val markedQuizIds: HashSet<String>,
    val createdDate: LocalDateTime
)