package com.quizit.authentication.fixture

fun createLoginRequest(
    username: String = USERNAME,
    password: String = PASSWORD
): com.quizit.authentication.dto.request.LoginRequest =
    com.quizit.authentication.dto.request.LoginRequest(
        username = username,
        password = password
    )

fun createLoginResponse(
    accessToken: String = ACCESS_TOKEN,
    refreshToken: String = REFRESH_TOKEN
): com.quizit.authentication.dto.response.LoginResponse =
    com.quizit.authentication.dto.response.LoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken
    )

fun createRefreshRequest(
    userId: String = ID,
    refreshToken: String = REFRESH_TOKEN
): com.quizit.authentication.dto.request.RefreshRequest =
    com.quizit.authentication.dto.request.RefreshRequest(
        userId = userId,
        refreshToken = refreshToken
    )

fun createRefreshResponse(
    accessToken: String = ACCESS_TOKEN,
    refreshToken: String = REFRESH_TOKEN
): com.quizit.authentication.dto.response.RefreshResponse =
    com.quizit.authentication.dto.response.RefreshResponse(
        accessToken = accessToken,
        refreshToken = refreshToken
    )