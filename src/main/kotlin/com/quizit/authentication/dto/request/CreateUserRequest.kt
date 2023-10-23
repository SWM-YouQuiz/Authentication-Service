package com.quizit.authentication.dto.request

import com.quizit.authentication.domain.enum.Provider

data class CreateUserRequest(
    val email: String,
    val username: String,
    val image: String,
    val allowPush: Boolean,
    val dailyTarget: Int,
    val provider: Provider
)