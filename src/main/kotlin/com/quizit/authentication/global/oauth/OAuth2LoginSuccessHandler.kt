package com.quizit.authentication.global.oauth

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.core.DefaultJwtProvider
import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.domain.OAuth2UserInfo
import com.quizit.authentication.domain.RefreshToken
import com.quizit.authentication.dto.request.CreateUserRequest
import com.quizit.authentication.exception.UserNotFoundException
import com.quizit.authentication.global.annotation.Handler
import com.quizit.authentication.repository.TokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.onErrorResume
import java.net.URI
import java.time.Duration

@Handler
class OAuth2LoginSuccessHandler(
    private val tokenRepository: TokenRepository,
    private val userClient: UserClient,
    private val jwtProvider: DefaultJwtProvider,
    @Value("\${url.frontend}")
    private val frontendUrl: String,
    @Value("\${jwt.refreshTokenExpire}")
    private val expire: Long,
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange, authentication: Authentication
    ): Mono<Void> {
        val oAuth2User = authentication.principal as OAuth2UserInfo
        var isSignUp = false

        return userClient.getUserByEmailAndProvider(oAuth2User.email, oAuth2User.provider)
            .onErrorResume(UserNotFoundException::class) {
                isSignUp = true

                userClient.createUser(
                    CreateUserRequest(
                        email = oAuth2User.email,
                        username = oAuth2User.name!!,
                        image = (1..6).map { "https://quizit-storage.s3.ap-northeast-2.amazonaws.com/character$it.svg" }
                            .random(),
                        allowPush = true,
                        dailyTarget = 5,
                        provider = oAuth2User.provider
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
                    webFilterExchange.exchange.response.apply {
                        statusCode = HttpStatus.FOUND
                        headers.location =
                            URI("$frontendUrl/login-redirection?isSignUp=$isSignUp&accessToken=${accessToken}")
                        cookies.set(
                            "refreshToken", ResponseCookie.from("refreshToken", refreshToken)
                                .path("$frontendUrl/")
                                .httpOnly(true)
                                .secure(true)
                                .maxAge(Duration.ofMinutes(expire))
                                .build()
                        )
                    }.setComplete()
                )
            }
    }
}