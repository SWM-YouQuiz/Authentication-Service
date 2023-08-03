package com.youquiz.authentication.handler

import com.youquiz.authentication.dto.LoginRequest
import com.youquiz.authentication.dto.RefreshRequest
import com.youquiz.authentication.global.config.awaitAuthentication
import com.youquiz.authentication.service.AuthenticationService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class AuthenticationHandler(
    private val authenticationService: AuthenticationService
) {
    suspend fun login(request: ServerRequest): ServerResponse =
        request.awaitBody<LoginRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(authenticationService.login(it))
        }

    suspend fun logout(request: ServerRequest): ServerResponse =
        with(request.awaitAuthentication()) {
            authenticationService.logout(id)

            ServerResponse.ok().buildAndAwait()
        }

    suspend fun refresh(request: ServerRequest): ServerResponse =
        request.awaitBody<RefreshRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(authenticationService.refresh(it))
        }
}