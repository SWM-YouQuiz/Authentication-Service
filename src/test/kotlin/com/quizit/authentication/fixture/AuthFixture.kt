package com.quizit.authentication.fixture

import com.quizit.authentication.dto.request.RefreshRequest
import com.quizit.authentication.dto.response.RefreshResponse

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