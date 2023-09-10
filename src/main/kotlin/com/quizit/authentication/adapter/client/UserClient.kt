package com.quizit.authentication.adapter.client

import com.quizit.authentication.dto.request.MatchPasswordRequest
import com.quizit.authentication.dto.response.MatchPasswordResponse
import com.quizit.authentication.dto.response.UserResponse
import com.quizit.authentication.exception.UserNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class UserClient(
    private val webClient: WebClient,
    @Value("\${url.service.user}")
    private val url: String
) {
    suspend fun getUserByUsername(username: String): UserResponse =
        webClient.get()
            .uri("$url/api/user/user/username/{username}", username)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<UserResponse>()

    suspend fun matchPassword(username: String, request: MatchPasswordRequest): MatchPasswordResponse =
        webClient.post()
            .uri("$url/api/user/user/username/{username}/match-password", username)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<MatchPasswordResponse>()
}