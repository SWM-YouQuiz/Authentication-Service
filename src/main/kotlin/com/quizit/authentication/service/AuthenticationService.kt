package com.quizit.authentication.service

import com.github.jwt.core.DefaultJwtProvider
import com.quizit.authentication.domain.RefreshToken
import com.quizit.authentication.dto.request.RefreshRequest
import com.quizit.authentication.dto.response.RefreshResponse
import com.quizit.authentication.exception.InvalidAccessException
import com.quizit.authentication.exception.TokenNotFoundException
import com.quizit.authentication.repository.TokenRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticationService(
    private val tokenRepository: TokenRepository,
    private val jwtProvider: DefaultJwtProvider,
) {
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
                    RefreshToken(
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