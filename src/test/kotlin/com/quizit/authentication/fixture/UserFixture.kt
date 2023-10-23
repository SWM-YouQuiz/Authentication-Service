package com.quizit.authentication.fixture

import com.quizit.authentication.domain.enum.Provider
import com.quizit.authentication.domain.enum.Role
import com.quizit.authentication.dto.response.UserResponse
import java.time.LocalDateTime

const val EMAIL = "email"
const val USERNAME = "username"
const val IMAGE = "image"
const val LEVEL = 2
val ROLE = Role.USER
const val ALLOW_PUSH = true
const val DAILY_TARGET = 10
const val ANSWER_RATE = 50.0
val PROVIDER = Provider.GOOGLE
val CORRECT_QUIZ_IDS = hashSetOf("1")
val INCORRECT_QUIZ_IDS = hashSetOf("1")
val MARKED_QUIZ_IDS = hashSetOf("1")

fun createUserResponse(
    id: String = ID,
    email: String = EMAIL,
    username: String = USERNAME,
    image: String? = IMAGE,
    level: Int = LEVEL,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    provider: Provider = PROVIDER,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: HashSet<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: HashSet<String> = INCORRECT_QUIZ_IDS,
    markedQuizIds: HashSet<String> = MARKED_QUIZ_IDS,
): UserResponse =
    UserResponse(
        id = id,
        email = email,
        username = username,
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