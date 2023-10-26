package com.quizit.authentication.adapter.client

import com.quizit.authentication.domain.GoogleOAuth2UserInfo
import com.quizit.authentication.global.annotation.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Client
class GoogleClient(
    private val webClient: WebClient,
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    private val clientSecret: String,
) {
    fun getOAuth2UserByToken(token: String): Mono<GoogleOAuth2UserInfo> =
        webClient.post()
            .uri("GET https://www.googleapis.com/oauth2/v3/userinfo")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .bodyToMono<Map<String, *>>()
            .map { GoogleOAuth2UserInfo(it) }

    fun getTokenResponseByCodeAndRedirectUri(code: String, redirectUri: String): Mono<Map<String, Any>> =
        webClient.post()
            .uri(
                "https://oauth2.googleapis.com/token?grant_type=authorization_code&client_id={clientId}&client_secret={clientSecret}&code={code}&redirect_uri=$redirectUri",
                clientId, clientSecret, code, redirectUri
            )
            .retrieve()
            .bodyToMono<Map<String, Any>>()

    fun revokeByToken(token: String): Mono<Void> =
        webClient.post()
            .uri("POST https://oauth2.googleapis.com/revoke?token={token}", token)
            .retrieve()
            .bodyToMono()
}