package com.youquiz.authentication.handler

import com.github.jwt.authentication.JwtAuthentication
import com.youquiz.authentication.dto.LoginRequest
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
        with(request.awaitPrincipal() as JwtAuthentication) {
            authenticationService.logout(id)
            ServerResponse.ok().buildAndAwait()
        }
}