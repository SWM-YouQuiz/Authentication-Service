package com.quizit.authentication.handler

import com.quizit.authentication.global.annotation.Handler
import com.quizit.authentication.global.oauth.AppleOAuth2Provider
import com.quizit.authentication.service.AppleOAuth2Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Handler
class AppleOAuth2Handler(
    private val appleOAuth2Service: AppleOAuth2Service,
    private val appleOAuth2Provider: AppleOAuth2Provider,
    @Value("\${url.frontend}")
    private val frontendUrl: String
) {
    fun revoke(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.status(HttpStatus.FOUND)
            .location(
                URI.create(
                    "https://appleid.apple.com/auth/authorize?response_mode=form_post&response_type=code&client_id=${appleOAuth2Provider.clientId}&scope=name%20email&&redirect_uri=$frontendUrl/api/auth/oauth2/redirect/apple/revoke"
                )
            ).build()

    fun loginRedirect(request: ServerRequest): Mono<ServerResponse> =
        request.formData()
            .flatMap { appleOAuth2Service.loginRedirect(it) }

    fun revokeRedirect(request: ServerRequest): Mono<ServerResponse> =
        request.formData()
            .flatMap { appleOAuth2Service.revokeRedirect(it) }
}