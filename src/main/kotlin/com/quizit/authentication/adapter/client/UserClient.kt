package com.quizit.authentication.adapter.client

import com.quizit.authentication.dto.request.CreateUserRequest
import com.quizit.authentication.dto.request.MatchPasswordRequest
import com.quizit.authentication.dto.response.MatchPasswordResponse
import com.quizit.authentication.dto.response.UserResponse
import com.quizit.authentication.exception.OAuthLoginException
import com.quizit.authentication.exception.UserNotFoundException
import com.quizit.authentication.exception.UsernameAlreadyExistException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Client
class UserClient(
    private val webClient: WebClient,
    @Value("\${url.service.user}")
    private val url: String
) {
    fun getUserByUsername(username: String): Mono<UserResponse> =
        webClient.get()
            .uri("$url/user/username/{username}", username)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { Mono.error(UserNotFoundException()) }
            .bodyToMono<UserResponse>()

    fun matchPassword(username: String, request: MatchPasswordRequest): Mono<MatchPasswordResponse> =
        webClient.post()
            .uri("$url/user/username/{username}/match-password", username)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { Mono.error(UserNotFoundException()) }
            .onStatus(HttpStatus.BAD_REQUEST::equals) { Mono.error(OAuthLoginException()) }
            .bodyToMono<MatchPasswordResponse>()

    fun createUser(request: CreateUserRequest): Mono<UserResponse> =
        webClient.post()
            .uri("$url/user")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus.CONFLICT::equals) { Mono.error(UsernameAlreadyExistException()) }
            .bodyToMono<UserResponse>()
}