package com.youquiz.authentication.domain

import com.youquiz.authentication.domain.enum.Role

data class User(
    val id: Long,
    val username: String,
    val password: String,
    val role: Role
)