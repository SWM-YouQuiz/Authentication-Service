package com.youquiz.authentication.adapter.client

import com.youquiz.authentication.domain.User
import com.youquiz.authentication.exception.UserNotFoundException
import com.youquiz.authentication.global.config.userServiceClient
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class UserClient(
    private val webClient: WebClient
) {
    suspend fun findByUsername(username: String) =
        webClient.get()
            .uri("$userServiceClient/user/username/{username}", username)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<User>()
}