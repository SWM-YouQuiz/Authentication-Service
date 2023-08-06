package com.quizit.authentication.adapter.client

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
    suspend fun getUserByUsername(username: String): com.quizit.authentication.dto.response.GetUserByUsernameResponse =
        webClient.get()
            .uri("$url/api/user/username/{username}", username)
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals
            ) { throw com.quizit.authentication.exception.UserNotFoundException() }
            .awaitBody<com.quizit.authentication.dto.response.GetUserByUsernameResponse>()

    suspend fun getPasswordByUsername(
        username: String
    ): com.quizit.authentication.dto.response.GetPasswordByUsernameResponse =
        webClient.get()
            .uri("$url/api/user/username/{username}/password", username)
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals
            ) { throw com.quizit.authentication.exception.UserNotFoundException() }
            .awaitBody<com.quizit.authentication.dto.response.GetPasswordByUsernameResponse>()
}