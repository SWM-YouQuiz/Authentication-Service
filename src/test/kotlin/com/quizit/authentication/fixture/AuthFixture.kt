package com.quizit.authentication.fixture

import com.quizit.authentication.dto.response.RefreshResponse

fun createRefreshResponse(
    accessToken: String = ACCESS_TOKEN,
): RefreshResponse =
    RefreshResponse(
        accessToken = accessToken,
    )