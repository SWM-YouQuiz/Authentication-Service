package com.quizit.authentication.fixture

import com.quizit.authentication.domain.enum.Provider
import com.quizit.authentication.domain.enum.Role
import com.quizit.authentication.dto.request.MatchPasswordRequest
import com.quizit.authentication.dto.response.MatchPasswordResponse
import com.quizit.authentication.dto.response.UserResponse
import java.time.LocalDateTime

const val USERNAME = "earlgrey02@github.com"
const val NICKNAME = "earlgrey02"
const val PASSWORD = "root"
const val IMAGE = "http://localhost:8080/image.jpg"
const val LEVEL = 2
val ROLE = Role.USER
const val ALLOW_PUSH = true
const val DAILY_TARGET = 10
const val ANSWER_RATE = 50.0
val PROVIDER = null
val CORRECT_QUIZ_IDS = hashSetOf("1")
val INCORRECT_QUIZ_IDS = hashSetOf("1")
val MARKED_QUIZ_IDS = hashSetOf("1")
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
    image: String? = IMAGE,
    level: Int = LEVEL,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    provider: Provider? = PROVIDER,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: HashSet<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: HashSet<String> = INCORRECT_QUIZ_IDS,
    markedQuizIds: HashSet<String> = MARKED_QUIZ_IDS,
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
        provider = provider,
        createdDate = createdDate,
        correctQuizIds = correctQuizIds,
        incorrectQuizIds = incorrectQuizIds,
        markedQuizIds = markedQuizIds
    )