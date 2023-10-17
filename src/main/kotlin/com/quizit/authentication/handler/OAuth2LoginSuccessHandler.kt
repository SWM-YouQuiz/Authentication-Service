package com.quizit.authentication.handler

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.core.DefaultJwtProvider
import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.domain.OAuth2UserInfo
import com.quizit.authentication.domain.Token
import com.quizit.authentication.repository.TokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI

@Component
class OAuth2LoginSuccessHandler(
    private val tokenRepository: TokenRepository,
    private val userClient: UserClient,
    private val jwtProvider: DefaultJwtProvider,
    @Value("\${spring.security.oauth2.redirectUrl}")
    private val url: String
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange, authentication: Authentication
    ): Mono<Void> =
        userClient.getUserByUsername((authentication.principal as OAuth2UserInfo).email)
            .flatMap {
                val jwtAuthentication = DefaultJwtAuthentication(
                    id = it.id,
                    authorities = listOf(SimpleGrantedAuthority(it.role.name))
                )
                val accessToken = jwtProvider.createAccessToken(jwtAuthentication)
                val refreshToken = jwtProvider.createRefreshToken(jwtAuthentication)

                tokenRepository.save(
                    Token(
                        userId = it.id,
                        content = refreshToken
                    )
                ).then(
                    webFilterExchange.exchange.response.apply {
                        statusCode = HttpStatus.FOUND
                        headers.location =
                            URI("$url?accessToken=$accessToken&refreshToken=$refreshToken&id=${it.id}")
                    }.setComplete()
                )
            }
}