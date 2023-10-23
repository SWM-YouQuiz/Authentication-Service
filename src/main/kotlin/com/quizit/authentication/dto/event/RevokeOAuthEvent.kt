package com.quizit.authentication.dto.event

import com.quizit.authentication.domain.enum.Provider

data class RevokeOAuthEvent(
    val email: String,
    val provider: Provider
)