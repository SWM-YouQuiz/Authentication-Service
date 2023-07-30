package com.youquiz.authentication.service

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.core.JwtProvider
import com.youquiz.authentication.adapter.client.UserClient
import com.youquiz.authentication.domain.Token
import com.youquiz.authentication.dto.LoginRequest
import com.youquiz.authentication.dto.LoginResponse
import com.youquiz.authentication.dto.RefreshRequest
import com.youquiz.authentication.dto.RefreshResponse
import com.youquiz.authentication.exception.InvalidAccessException
import com.youquiz.authentication.exception.PasswordNotMatchException
import com.youquiz.authentication.exception.TokenNotFoundException
import com.youquiz.authentication.repository.TokenRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val tokenRepository: TokenRepository,
    private val userClient: UserClient,
    private val jwtProvider: JwtProvider,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun login(request: LoginRequest): LoginResponse = coroutineScope {
        val getPasswordByUsernameDeferred = async { userClient.getPasswordByUsername(request.username) }
        val findByUsernameDeferred = async { userClient.findByUsername(request.username) }

        val getUserPasswordByUsernameResponse = getPasswordByUsernameDeferred.await()
        val findUserByUsernameResponse = findByUsernameDeferred.await()

        if (passwordEncoder.matches(request.password, getUserPasswordByUsernameResponse.password)) {
            run {
                DefaultJwtAuthentication(
                    id = findUserByUsernameResponse.id,
                    authorities = listOf(SimpleGrantedAuthority(findUserByUsernameResponse.role))
                )
            }.let {
                val accessToken = jwtProvider.createAccessToken(it)
                val refreshToken = jwtProvider.createRefreshToken(it)

                tokenRepository.save(
                    Token(
                        userId = it.id,
                        content = refreshToken
                    )
                )

                LoginResponse(accessToken = accessToken, refreshToken = refreshToken)
            }
        } else throw PasswordNotMatchException()
    }

    suspend fun logout(userId: String) {
        tokenRepository.deleteByUserId(userId)
    }

    suspend fun refresh(request: RefreshRequest): RefreshResponse {
        val refreshToken = tokenRepository.findByUserId(request.userId)?.content ?: throw TokenNotFoundException()

        jwtProvider.getAuthentication(refreshToken).let {
            if (request.refreshToken == refreshToken) {
                val newAccessToken = jwtProvider.createAccessToken(it)
                val newRefreshToken = jwtProvider.createRefreshToken(it)

                tokenRepository.save(
                    Token(
                        userId = it.id,
                        content = newRefreshToken
                    )
                )

                return RefreshResponse(
                    accessToken = newAccessToken,
                    refreshToken = newRefreshToken
                )
            } else {
                tokenRepository.deleteByUserId(it.id)
                throw InvalidAccessException()
            }
        }
    }
}