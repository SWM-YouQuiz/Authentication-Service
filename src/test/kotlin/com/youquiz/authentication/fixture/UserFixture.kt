package com.youquiz.authentication.fixture

import com.youquiz.authentication.dto.FindUserByUsernameResponse
import com.youquiz.authentication.dto.GetUserPasswordByUsernameResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

const val USERNAME = "earlgrey02@github.com"
const val NICKNAME = "earlgrey02"
const val PASSWORD = "root"
val ROLE = "USER"
const val ALLOW_PUSH = true
val CREATED_DATE = LocalDateTime.now()!!
val CORRECT_QUIZ_IDS = mutableSetOf<String>()
val INCORRECT_QUIZ_IDS = mutableSetOf<String>()
val LIKED_QUIZ_IDS = mutableSetOf<String>()
const val INVALID_USERNAME = "test"
const val INVALID_PASSWORD = "test"

fun createGetUserPasswordByUsernameResponse(
    password: String = PASSWORD
): GetUserPasswordByUsernameResponse =
    GetUserPasswordByUsernameResponse(BCryptPasswordEncoder().encode(password))

fun createFindUserByUsernameResponse(
    id: String = ID,
    username: String = USERNAME,
    nickname: String = NICKNAME,
    role: String = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: MutableSet<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: MutableSet<String> = INCORRECT_QUIZ_IDS,
    likedQuizIds: MutableSet<String> = LIKED_QUIZ_IDS
): FindUserByUsernameResponse =
    FindUserByUsernameResponse(
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