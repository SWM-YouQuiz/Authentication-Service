package com.youquiz.authentication.handler

import com.youquiz.authentication.dto.LoginRequest
import com.youquiz.authentication.service.AuthenticationService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class AuthenticationHandler(
    private val authenticationService: AuthenticationService
) {
    suspend fun login(serverRequest: ServerRequest): ServerResponse {
        val loginRequest = serverRequest.awaitBody<LoginRequest>()

        return ServerResponse.ok().bodyValueAndAwait(authenticationService.login(loginRequest))
    }
}