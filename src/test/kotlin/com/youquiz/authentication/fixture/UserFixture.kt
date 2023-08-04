package com.youquiz.authentication.fixture

import com.youquiz.authentication.dto.GetPasswordByUsernameResponse
import com.youquiz.authentication.dto.GetUserByUsernameResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

const val USERNAME = "earlgrey02@github.com"
const val NICKNAME = "earlgrey02"
const val PASSWORD = "root"
const val ROLE = "USER"
const val ALLOW_PUSH = true
val CREATED_DATE = LocalDateTime.now()!!
val CORRECT_QUIZ_IDS = setOf("quiz_1")
val INCORRECT_QUIZ_IDS = setOf("quiz_2")
val LIKED_QUIZ_IDS = setOf("quiz_3")

const val INVALID_USERNAME = "invalid_username"
const val INVALID_PASSWORD = "invalid_password"

fun createGetUserPasswordByUsernameResponse(
    password: String = PASSWORD
): GetPasswordByUsernameResponse =
    GetPasswordByUsernameResponse(BCryptPasswordEncoder().encode(password))

fun createFindUserByUsernameResponse(
    id: String = ID,
    username: String = USERNAME,
    nickname: String = NICKNAME,
    role: String = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: Set<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: Set<String> = INCORRECT_QUIZ_IDS,
    likedQuizIds: Set<String> = LIKED_QUIZ_IDS
): GetUserByUsernameResponse =
    GetUserByUsernameResponse(
        id = id,
        username = username,
        nickname = nickname,
        role = role,
        allowPush = allowPush,
        createdDate = createdDate,
        correctQuizIds = correctQuizIds,
        incorrectQuizIds = incorrectQuizIds,
        likedQuizIds = likedQuizIds
    )