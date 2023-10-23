package com.quizit.authentication.handler

import com.quizit.authentication.dto.request.RefreshRequest
import com.quizit.authentication.global.annotation.Handler
import com.quizit.authentication.global.util.authentication
import com.quizit.authentication.service.AuthenticationService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Handler
class AuthenticationHandler(
    private val authenticationService: AuthenticationService
) {
    fun logout(request: ServerRequest): Mono<ServerResponse> =
        request.authentication()
            .flatMap {
                ServerResponse.ok()
                    .body(authenticationService.logout(it.id))
            }

    fun refresh(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<RefreshRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(authenticationService.refresh(it))
            }
}