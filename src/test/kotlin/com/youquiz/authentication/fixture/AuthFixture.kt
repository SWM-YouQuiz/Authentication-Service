package com.youquiz.authentication.fixture

import com.youquiz.authentication.dto.request.LoginRequest
import com.youquiz.authentication.dto.request.RefreshRequest
import com.youquiz.authentication.dto.response.LoginResponse
import com.youquiz.authentication.dto.response.RefreshResponse

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
    refreshToken: String = REFRESH_TOKEN
): LoginResponse =
    LoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken
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