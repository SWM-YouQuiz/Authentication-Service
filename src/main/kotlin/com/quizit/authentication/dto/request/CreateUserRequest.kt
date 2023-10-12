package com.quizit.authentication.dto.request

import com.quizit.authentication.domain.enum.Provider

data class CreateUserRequest(
    val username: String,
    val password: String?,
    val nickname: String,
    val image: String?,
    val allowPush: Boolean,
    val dailyTarget: Int,
    val provider: Provider?
)