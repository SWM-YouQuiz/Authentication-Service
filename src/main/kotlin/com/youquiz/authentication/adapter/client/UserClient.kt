package com.youquiz.authentication.adapter.client

import com.youquiz.authentication.dto.GetPasswordByUsernameResponse
import com.youquiz.authentication.dto.GetUserByUsernameResponse
import com.youquiz.authentication.exception.UserNotFoundException
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
    suspend fun getUserByUsername(username: String): GetUserByUsernameResponse =
        webClient.get()
            .uri("$url/api/user/username/{username}", username)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<GetUserByUsernameResponse>()

    suspend fun getPasswordByUsername(username: String): GetPasswordByUsernameResponse =
        webClient.get()
            .uri("$url/api/user/username/{username}/password", username)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<GetPasswordByUsernameResponse>()
}