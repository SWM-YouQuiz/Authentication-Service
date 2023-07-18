package com.youquiz.authentication.service

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.core.JwtProvider
import com.youquiz.authentication.adapter.client.UserClient
import com.youquiz.authentication.dto.LoginRequest
import com.youquiz.authentication.dto.LoginResponse
import com.youquiz.authentication.exception.PasswordNotMatchException
import com.youquiz.authentication.repository.TokenRepository
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
    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        val user = userClient.findByUsername(loginRequest.username)

        if (passwordEncoder.matches(loginRequest.password, user.password)) {
            val authentication = user.run {
                DefaultJwtAuthentication(id = id, authorities = listOf(SimpleGrantedAuthority(role.name)))
            }
            val accessToken = jwtProvider.createAccessToken(authentication)
            val refreshToken = jwtProvider.createRefreshToken(authentication)

            return LoginResponse(accessToken = accessToken, refreshToken = refreshToken)
        } else throw PasswordNotMatchException()
    }

    suspend fun logout(userId: Long) {
        tokenRepository.deleteByUserId(userId)
    }
}