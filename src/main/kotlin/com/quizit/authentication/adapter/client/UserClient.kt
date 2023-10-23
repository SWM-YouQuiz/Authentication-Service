package com.quizit.authentication.adapter.client

import com.quizit.authentication.domain.enum.Provider
import com.quizit.authentication.dto.request.CreateUserRequest
import com.quizit.authentication.dto.response.UserResponse
import com.quizit.authentication.exception.UserAlreadyExistException
import com.quizit.authentication.exception.UserNotFoundException
import com.quizit.authentication.global.annotation.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Client
class UserClient(
    private val webClient: WebClient,
    @Value("\${url.service.user}")
    private val url: String
) {
    fun getUserByEmailAndProvider(email: String, provider: Provider): Mono<UserResponse> =
        webClient.get()
            .uri("$url/user/email/{email}?provider={provider}", email, provider)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { Mono.error(UserNotFoundException()) }
            .bodyToMono<UserResponse>()

    fun createUser(request: CreateUserRequest): Mono<UserResponse> =
        webClient.post()
            .uri("$url/user")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus.CONFLICT::equals) { Mono.error(UserAlreadyExistException()) }
            .bodyToMono<UserResponse>()
}