package com.quizit.authentication.fixture

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.config.JwtConfiguration
import com.quizit.authentication.domain.Token
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

val SECRET_KEY = (1..100).map { ('a'..'z').random() }.joinToString("")
const val ACCESS_TOKEN_EXPIRE = 5L
const val REFRESH_TOKEN_EXPIRE = 10L
val AUTHORITIES = listOf(SimpleGrantedAuthority(ROLE))
const val INVALID_TOKEN = "test"
val jwtProvider = JwtConfiguration().jwtProvider(
    secretKey = SECRET_KEY,
    accessTokenExpire = ACCESS_TOKEN_EXPIRE,
    refreshTokenExpire = REFRESH_TOKEN_EXPIRE
)
val ACCESS_TOKEN = jwtProvider.createAccessToken(createJwtAuthentication())
val REFRESH_TOKEN = jwtProvider.createRefreshToken(createJwtAuthentication())

fun createJwtAuthentication(
    id: String = ID,
    authorities: List<GrantedAuthority> = AUTHORITIES
): DefaultJwtAuthentication =
    DefaultJwtAuthentication(
        id = id,
        authorities = authorities
    )

fun createToken(
    userId: String = ID,
    content: String = REFRESH_TOKEN
): Token = Token(
    userId = userId,
    content = content
)