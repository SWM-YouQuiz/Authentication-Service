package com.quizit.authentication.handler

import com.quizit.authentication.global.annotation.Handler
import com.quizit.authentication.global.util.queryParamNotNull
import com.quizit.authentication.service.KakaoOAuth2Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Handler
class KakaoOAuth2Handler(
    private val kakaoOAuth2Service: KakaoOAuth2Service,
    @Value("\${spring.security.oauth2.client.registration.kakao.client-id}")
    private val clientId: String,
    @Value("\${url.frontend}")
    private val frontendUrl: String
) {
    fun revoke(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.status(HttpStatus.FOUND)
            .location(
                URI.create(
                    "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=$clientId&scope=profile_nickname%20account_email&redirect_uri=$frontendUrl/api/auth/oauth2/redirect/kakao/revoke"
                )
            ).build()

    fun revokeRedirect(request: ServerRequest): Mono<ServerResponse> =
        kakaoOAuth2Service.revokeRedirect(request.queryParamNotNull("code"))
}