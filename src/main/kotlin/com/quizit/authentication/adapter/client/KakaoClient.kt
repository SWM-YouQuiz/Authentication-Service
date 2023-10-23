package com.quizit.authentication.adapter.client

import com.quizit.authentication.domain.KakaoOAuth2UserInfo
import com.quizit.authentication.global.annotation.Client
import com.quizit.authentication.global.util.multiValueMapOf
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Client
class KakaoClient(
    private val webClient: WebClient,
    @Value("\${spring.security.oauth2.client.registration.kakao.client-id}")
    private val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.kakao.client-secret}")
    private val clientSecret: String,
) {
    fun getOAuth2UserByToken(token: String): Mono<KakaoOAuth2UserInfo> =
        webClient.post()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .bodyToMono<Map<String, *>>()
            .map { KakaoOAuth2UserInfo(it) }

    fun getTokenResponseByCodeAndRedirectUri(code: String, redirectUri: String): Mono<Map<String, Any>> =
        webClient.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                multiValueMapOf(
                    "client_id" to clientId,
                    "client_secret" to clientSecret,
                    "code" to code,
                    "redirect_uri" to redirectUri,
                    "grant_type" to "authorization_code"
                )
            )
            .retrieve()
            .bodyToMono<Map<String, Any>>()

    fun revokeByToken(token: String): Mono<Void> =
        webClient.post()
            .uri("https://kapi.kakao.com/v1/user/unlink")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .bodyToMono()
}