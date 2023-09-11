package com.quizit.authentication.fixture

import com.quizit.authentication.domain.enum.Role
import com.quizit.authentication.dto.request.MatchPasswordRequest
import com.quizit.authentication.dto.response.MatchPasswordResponse
import com.quizit.authentication.dto.response.UserResponse
import java.time.LocalDateTime

const val USERNAME = "earlgrey02@github.com"
const val NICKNAME = "earlgrey02"
const val PASSWORD = "root"
const val IMAGE = "test"
const val LEVEL = 2
val ROLE = Role.USER
const val ALLOW_PUSH = true
const val DAILY_TARGET = 10
const val ANSWER_RATE = 50.0
val CORRECT_QUIZ_IDS = setOf("quiz_1")
val INCORRECT_QUIZ_IDS = setOf("quiz_2")
val MARKED_QUIZ_IDS = setOf("quiz_3")
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

fun createUserResponse(
    id: String = ID,
    username: String = USERNAME,
    nickname: String = NICKNAME,
    image: String = IMAGE,
    level: Int = LEVEL,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: Set<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: Set<String> = INCORRECT_QUIZ_IDS,
    markedQuizIds: Set<String> = MARKED_QUIZ_IDS,
): UserResponse =
    UserResponse(
        id = id,
        username = username,
        nickname = nickname,
        image = image,
        level = level,
        role = role,
        allowPush = allowPush,
        dailyTarget = dailyTarget,
        answerRate = answerRate,
        createdDate = createdDate,
        correctQuizIds = correctQuizIds,
        incorrectQuizIds = incorrectQuizIds,
        markedQuizIds = markedQuizIds
    )