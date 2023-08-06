package com.quizit.authentication.handler

import com.quizit.authentication.global.config.awaitAuthentication
import com.quizit.authentication.service.AuthenticationService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class AuthenticationHandler(
    private val authenticationService: AuthenticationService
) {
    suspend fun login(request: ServerRequest): ServerResponse =
        request.awaitBody<com.quizit.authentication.dto.request.LoginRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(authenticationService.login(it))
        }

    suspend fun logout(request: ServerRequest): ServerResponse =
        with(request.awaitAuthentication()) {
            authenticationService.logout(id)

            ServerResponse.ok().buildAndAwait()
        }

    suspend fun refresh(request: ServerRequest): ServerResponse =
        request.awaitBody<com.quizit.authentication.dto.request.RefreshRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(authenticationService.refresh(it))
        }
}