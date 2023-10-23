package com.quizit.authentication.adapter.client

import com.quizit.authentication.domain.AppleOAuth2UserInfo
import com.quizit.authentication.global.annotation.Client
import com.quizit.authentication.global.oauth.AppleOAuth2Provider
import com.quizit.authentication.global.util.multiValueMapOf
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Client
class AppleClient(
    private val webClient: WebClient,
    private val appleOAuth2Provider: AppleOAuth2Provider,
    @Value("\${url.frontend}")
    private val frontendUrl: String
) {
    fun getIdTokenByCode(code: String): Mono<String> =
        getTokenResponseByCode(code)
            .map { it["id_token"] as String }

    fun getAccessTokenByCode(code: String): Mono<String> =
        getTokenResponseByCode(code)
            .map { it["access_token"] as String }

    fun getOAuth2UserByToken(token: String): Mono<AppleOAuth2UserInfo> =
        webClient.get()
            .uri("https://appleid.apple.com/auth/keys")
            .retrieve()
            .bodyToMono<Map<String, List<Map<String, String>>>>()
            .map {
                Jwts.parserBuilder()
                    .setSigningKey(appleOAuth2Provider.createPublicKey(token, it["keys"]!!))
                    .build()
                    .parseClaimsJws(token)
                    .body
            }
            .map {
                AppleOAuth2UserInfo(
                    email = it["email"] as String,
                    name = null
                )
            }

    private fun getTokenResponseByCode(code: String): Mono<Map<String, Any>> =
        webClient.post()
            .uri("https://appleid.apple.com/auth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                multiValueMapOf(
                    "client_id" to appleOAuth2Provider.clientId,
                    "client_secret" to appleOAuth2Provider.createClientSecret(),
                    "code" to code,
                    "redirect_uri" to "$frontendUrl/api/auth/oauth2/redirect/apple",
                    "grant_type" to "authorization_code"
                )
            )
            .retrieve()
            .bodyToMono<Map<String, Any>>()
}