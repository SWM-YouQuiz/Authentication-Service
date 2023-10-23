package com.quizit.authentication.handler

import com.quizit.authentication.global.annotation.Handler
import com.quizit.authentication.global.util.queryParamNotNull
import com.quizit.authentication.service.GoogleOAuth2Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Handler
class GoogleOAuth2Handler(
    private val googleOAuth2Service: GoogleOAuth2Service,
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val clientId: String,
    @Value("\${url.frontend}")
    private val frontendUrl: String
) {
    fun revoke(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.status(HttpStatus.FOUND)
            .location(
                URI.create(
                    "https://accounts.google.com/o/oauth2/v2/auth?access_type=offline&prompt=consent&response_type=code&client_id=$clientId&redirect_uri=$frontendUrl/api/auth/oauth2/redirect/google/revoke&scope=profile%20email"
                )
            ).build()

    fun revokeRedirect(request: ServerRequest): Mono<ServerResponse> =
        googleOAuth2Service.revokeRedirect(request.queryParamNotNull("code"))

}