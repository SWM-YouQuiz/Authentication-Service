package com.quizit.authentication.fixture

import com.quizit.authentication.dto.request.MatchPasswordRequest
import com.quizit.authentication.dto.response.GetUserByUsernameResponse
import com.quizit.authentication.dto.response.MatchPasswordResponse
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
const val IS_MATCHED = true
const val INVALID_USERNAME = "invalid_username"
const val INVALID_PASSWORD = "invalid_password"

fun createMatchPasswordRequest(
    password: String = PASSWORD
): MatchPasswordRequest =
    MatchPasswordRequest(password)

fun createMatchPasswordResponse(
    isMatched: Boolean = IS_MATCHED
): MatchPasswordResponse =
    MatchPasswordResponse(isMatched)

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