package com.youquiz.authentication.fixture

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.config.JwtConfiguration
import com.youquiz.authentication.domain.Token
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

val SECRET_KEY = (1..100).map { ('a'..'z').random() }.joinToString("")
const val ACCESS_TOKEN_EXPIRE = 5L
const val REFRESH_TOKEN_EXPIRE = 10L
val AUTHORITIES = listOf(SimpleGrantedAuthority(ROLE.name))
const val INVALID_TOKEN = "test"
val jwtProvider = JwtConfiguration().jwtProvider(
    secretKey = SECRET_KEY,
    accessTokenExpire = ACCESS_TOKEN_EXPIRE,
    refreshTokenExpire = REFRESH_TOKEN_EXPIRE
)

fun createJwtAuthentication(
    id: Long = ID,
    authorities: List<GrantedAuthority> = AUTHORITIES
): DefaultJwtAuthentication =
    DefaultJwtAuthentication(
        id = id,
        authorities = authorities
    )

fun createToken(
    userId: Long = ID,
    content: String = jwtProvider.createRefreshToken(createJwtAuthentication())
): Token = Token(
    userId = userId,
    content = content
)