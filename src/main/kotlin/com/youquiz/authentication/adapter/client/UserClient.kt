package com.youquiz.authentication.adapter.client

import com.youquiz.authentication.dto.FindUserByUsernameResponse
import com.youquiz.authentication.dto.GetUserPasswordByUsernameResponse
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
    suspend fun findByUsername(username: String): FindUserByUsernameResponse =
        webClient.get()
            .uri("$url/api/user/username/{username}", username)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<FindUserByUsernameResponse>()

    suspend fun getPasswordByUsername(username: String): GetUserPasswordByUsernameResponse =
        webClient.get()
            .uri("$url/api/user/username/{username}/password", username)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<GetUserPasswordByUsernameResponse>()
}