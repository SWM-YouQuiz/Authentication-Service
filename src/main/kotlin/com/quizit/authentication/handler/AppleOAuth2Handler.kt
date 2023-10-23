package com.quizit.authentication.handler

import com.quizit.authentication.global.annotation.Handler
import com.quizit.authentication.service.AppleOAuth2Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Handler
class AppleOAuth2Handler(
    private val appleOAuth2Service: AppleOAuth2Service,
) {
    fun loginRedirect(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.formData()
            .flatMap { appleOAuth2Service.loginRedirect(it) }
}