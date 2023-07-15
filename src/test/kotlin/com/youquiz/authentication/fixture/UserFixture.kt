package com.youquiz.authentication.fixture

import com.youquiz.authentication.domain.User
import com.youquiz.authentication.domain.enum.Role
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

const val ID = 1L
const val USERNAME = "earlgrey02"
const val PASSWORD = "root"
val ROLE = Role.USER
const val INVALID_USERNAME = "test"
const val INVALID_PASSWORD = "test"
val USER = User(
    id = ID,
    username = USERNAME,
    password = BCryptPasswordEncoder().encode(PASSWORD),
    role = ROLE
)