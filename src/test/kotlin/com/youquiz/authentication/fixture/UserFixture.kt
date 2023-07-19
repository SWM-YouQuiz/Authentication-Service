package com.youquiz.authentication.fixture

import com.youquiz.authentication.domain.User
import com.youquiz.authentication.domain.enum.Role
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

const val USERNAME = "earlgrey02"
const val PASSWORD = "root"
val ROLE = Role.USER
const val INVALID_USERNAME = "test"
const val INVALID_PASSWORD = "test"

fun createUser(
    id: Long = ID,
    username: String = USERNAME,
    password: String = PASSWORD,
    role: Role = ROLE
) = User(
    id = id,
    username = username,
    password = BCryptPasswordEncoder().encode(password),
    role = role
)