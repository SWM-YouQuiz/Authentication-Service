package com.quizit.authentication.service

import com.github.jwt.core.DefaultJwtProvider
import com.quizit.authentication.domain.RefreshToken
import com.quizit.authentication.dto.response.RefreshResponse
import com.quizit.authentication.exception.InvalidAccessException
import com.quizit.authentication.exception.InvalidTokenException
import com.quizit.authentication.exception.TokenNotFoundException
import com.quizit.authentication.repository.TokenRepository
import io.jsonwebtoken.JwtException
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

    fun refresh(token: String): Mono<Pair<RefreshResponse, String>> =
        with(
            try {
                jwtProvider.getAuthentication(token)
            } catch (ex: JwtException) {
                throw InvalidTokenException()
            }
        ) {
            tokenRepository.findByUserId(id)
                .switchIfEmpty(Mono.error(TokenNotFoundException()))
                .filter { token == it.content }
                .switchIfEmpty(
                    Mono.defer {
                        tokenRepository.deleteByUserId(id)
                            .then(Mono.error(InvalidAccessException()))
                    }
                )
                .flatMap {
                    val accessToken = jwtProvider.createAccessToken(this)
                    val refreshToken = jwtProvider.createRefreshToken(this)

                    tokenRepository.save(
                        RefreshToken(
                            userId = id,
                            content = refreshToken
                        )
                    ).thenReturn(
                        Pair(
                            RefreshResponse(accessToken = accessToken),
                            refreshToken
                        )
                    )
                }
        }
}