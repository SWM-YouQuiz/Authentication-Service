package com.quizit.authentication.fixture

import com.quizit.authentication.dto.request.LoginRequest
import com.quizit.authentication.dto.request.RefreshRequest
import com.quizit.authentication.dto.response.LoginResponse
import com.quizit.authentication.dto.response.RefreshResponse
import com.quizit.authentication.dto.response.UserResponse

fun createLoginRequest(
    username: String = USERNAME,
    password: String = PASSWORD
): LoginRequest =
    LoginRequest(
        username = username,
        password = password
    )

fun createLoginResponse(
    accessToken: String = ACCESS_TOKEN,
    refreshToken: String = REFRESH_TOKEN,
    user: UserResponse = createUserResponse()
): LoginResponse =
    LoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user
    )

fun createRefreshRequest(
    userId: String = ID,
    refreshToken: String = REFRESH_TOKEN
): RefreshRequest =
    RefreshRequest(
        userId = userId,
        refreshToken = refreshToken
    )

fun createRefreshResponse(
    accessToken: String = ACCESS_TOKEN,
    refreshToken: String = REFRESH_TOKEN
): RefreshResponse =
    RefreshResponse(
        accessToken = accessToken,
        refreshToken = refreshToken
    )