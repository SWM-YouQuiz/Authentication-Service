package com.quizit.authentication.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.core.DefaultJwtProvider
import com.quizit.authentication.adapter.client.AppleClient
import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.adapter.producer.AuthenticationProducer
import com.quizit.authentication.domain.AppleOAuth2UserInfo
import com.quizit.authentication.domain.OAuth2UserInfo
import com.quizit.authentication.domain.RefreshToken
import com.quizit.authentication.domain.enum.Provider
import com.quizit.authentication.dto.event.RevokeOAuthEvent
import com.quizit.authentication.dto.request.CreateUserRequest
import com.quizit.authentication.exception.UserNotFoundException
import com.quizit.authentication.global.util.component1
import com.quizit.authentication.repository.TokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.onErrorResume
import reactor.kotlin.core.publisher.switchIfEmpty
import java.net.URI
import java.time.Duration

@Service
class AppleOAuth2Service(
    private val appleClient: AppleClient,
    private val tokenRepository: TokenRepository,
    private val userClient: UserClient,
    private val authenticationProducer: AuthenticationProducer,
    private val objectMapper: ObjectMapper,
    private val jwtProvider: DefaultJwtProvider,
    @Value("\${url.frontend}")
    private val frontendUrl: String,
    @Value("\${jwt.refreshTokenExpire}")
    private val expire: Long,
) {
    fun loginRedirect(loginResponse: MultiValueMap<String, String>): Mono<ServerResponse> =
        Mono.justOrEmpty(loginResponse["user"]?.firstOrNull())
            .map {
                val user = objectMapper.readValue<Map<String, Any>>(it!!)
                val name = (user["name"] as Map<String, String>).run { get("lastName") + get("firstName") }
                val email = user["email"] as String

                AppleOAuth2UserInfo(
                    email = email,
                    name = name
                )
            }
            .switchIfEmpty {
                appleClient.getTokenResponseByCodeAndRedirectUri(
                    loginResponse["code"]!!.first(), "$frontendUrl/api/auth/oauth2/redirect/apple"
                )
                    .flatMap { appleClient.getOAuth2UserByToken(it["id_token"] as String) }
            }
            .flatMap { it.onAuthenticationSuccess() }

    fun revokeRedirect(loginResponse: MultiValueMap<String, String>): Mono<ServerResponse> =
        appleClient.getTokenResponseByCodeAndRedirectUri(
            loginResponse["code"]!!.first(), "$frontendUrl/api/auth/oauth2/redirect/apple/revoke"
        )
            .flatMap {
                Mono.zip(
                    appleClient.getOAuth2UserByToken(it["id_token"] as String)
                        .subscribeOn(Schedulers.boundedElastic()),
                    appleClient.revokeByToken(it["access_token"] as String)
                        .subscribeOn(Schedulers.boundedElastic())
                )
            }
            .flatMap { (oAuth2User) ->
                oAuth2User.run {
                    authenticationProducer.revokeOAuth(
                        RevokeOAuthEvent(
                            email = email,
                            provider = provider
                        )
                    )
                }
            }
            .then(Mono.defer {
                ServerResponse.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl))
                    .build()
            })

    fun OAuth2UserInfo.onAuthenticationSuccess(): Mono<ServerResponse> {
        var isSignUp = false

        return userClient.getUserByEmailAndProvider(email, provider)
            .onErrorResume(UserNotFoundException::class) {
                isSignUp = true

                userClient.createUser(
                    CreateUserRequest(
                        email = email,
                        username = name!!,
                        image = (1..6).map { "https://quizit-storage.s3.ap-northeast-2.amazonaws.com/character$it.svg" }
                            .random(),
                        allowPush = true,
                        dailyTarget = 5,
                        provider = Provider.APPLE
                    )
                )
            }
            .flatMap {
                val jwtAuthentication = DefaultJwtAuthentication(
                    id = it.id,
                    authorities = listOf(SimpleGrantedAuthority(it.role.name))
                )
                val accessToken = jwtProvider.createAccessToken(jwtAuthentication)
                val refreshToken = jwtProvider.createRefreshToken(jwtAuthentication)

                tokenRepository.save(
                    RefreshToken(
                        userId = it.id,
                        content = refreshToken
                    )
                ).then(
                    ServerResponse.status(HttpStatus.FOUND)
                        .location(
                            URI("$frontendUrl/login-redirection?isSignUp=$isSignUp&accessToken=${accessToken}")
                        )
                        .cookie(
                            ResponseCookie.from("refreshToken", refreshToken)
                                .path("/")
                                .httpOnly(true)
                                .secure(true)
                                .maxAge(Duration.ofMinutes(expire))
                                .build()
                        )
                        .build()
                )
            }
    }
}