package com.quizit.authentication.service

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.core.DefaultJwtProvider
import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.domain.Token
import com.quizit.authentication.dto.request.LoginRequest
import com.quizit.authentication.dto.request.MatchPasswordRequest
import com.quizit.authentication.dto.request.RefreshRequest
import com.quizit.authentication.dto.response.LoginResponse
import com.quizit.authentication.dto.response.RefreshResponse
import com.quizit.authentication.exception.InvalidAccessException
import com.quizit.authentication.exception.OAuthLoginException
import com.quizit.authentication.exception.PasswordNotMatchException
import com.quizit.authentication.exception.TokenNotFoundException
import com.quizit.authentication.global.util.component1
import com.quizit.authentication.global.util.component2
import com.quizit.authentication.repository.TokenRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class AuthenticationService(
    private val tokenRepository: TokenRepository,
    private val userClient: UserClient,
    private val jwtProvider: DefaultJwtProvider,
) {
    fun login(request: LoginRequest): Mono<LoginResponse> =
        with(request) {
            Mono.zip(
                userClient.getUserByUsername(username)
                    .subscribeOn(Schedulers.parallel()),
                userClient.matchPassword(username, MatchPasswordRequest(password))
                    .subscribeOn(Schedulers.parallel())
            ).filter { (user) -> user.provider == null }
                .switchIfEmpty(Mono.error(OAuthLoginException()))
                .filter { (_, matchPasswordResponse) -> matchPasswordResponse.isMatched }
                .switchIfEmpty(Mono.error(PasswordNotMatchException()))
                .map { (userResponse) ->
                    userResponse.let {
                        Pair(
                            it,
                            DefaultJwtAuthentication(
                                id = it.id,
                                authorities = listOf(SimpleGrantedAuthority(it.role.name))
                            )
                        )
                    }
                }
                .flatMap { (userResponse, authentication) ->
                    val accessToken = jwtProvider.createAccessToken(authentication)
                    val refreshToken = jwtProvider.createRefreshToken(authentication)

                    tokenRepository.save(
                        Token(
                            userId = authentication.id,
                            content = refreshToken
                        )
                    ).thenReturn(
                        LoginResponse(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            user = userResponse
                        )
                    )
                }
        }

    fun logout(userId: String): Mono<Void> =
        tokenRepository.deleteByUserId(userId)
            .then()

    fun refresh(request: RefreshRequest): Mono<RefreshResponse> =
        tokenRepository.findByUserId(request.userId)
            .switchIfEmpty(Mono.error(TokenNotFoundException()))
            .map { jwtProvider.getAuthentication(it.content) }
            .filter { request.refreshToken == it.token }
            .switchIfEmpty(
                tokenRepository.deleteByUserId(request.userId)
                    .then(Mono.error(InvalidAccessException()))
            )
            .flatMap {
                val accessToken = jwtProvider.createAccessToken(it)
                val refreshToken = jwtProvider.createRefreshToken(it)

                tokenRepository.save(
                    Token(
                        userId = it.id,
                        content = refreshToken
                    )
                ).thenReturn(
                    RefreshResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
            }
}