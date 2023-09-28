package com.quizit.authentication.fixture

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.config.JwtConfiguration
import com.quizit.authentication.domain.Token
import com.quizit.authentication.domain.enum.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

val SECRET_KEY = (1..100).map { ('a'..'z').random() }.joinToString("")
const val ACCESS_TOKEN_EXPIRE = 5L
const val REFRESH_TOKEN_EXPIRE = 10L
val AUTHORITIES = listOf(SimpleGrantedAuthority(Role.USER.name))
const val INVALID_TOKEN = "invalid_token"
val jwtProvider = JwtConfiguration().jwtProvider(SECRET_KEY, ACCESS_TOKEN_EXPIRE, REFRESH_TOKEN_EXPIRE)
val ACCESS_TOKEN = jwtProvider.createAccessToken(createJwtAuthentication())
val REFRESH_TOKEN = jwtProvider.createRefreshToken(createJwtAuthentication())

fun createJwtAuthentication(
    id: String = ID,
    authorities: List<GrantedAuthority> = AUTHORITIES,
    token: String? = null
): DefaultJwtAuthentication =
    DefaultJwtAuthentication(
        id = id,
        authorities = authorities,
        token = token
    )

fun createToken(
    userId: String = ID,
    content: String = REFRESH_TOKEN
): Token = Token(
    userId = userId,
    content = content
)