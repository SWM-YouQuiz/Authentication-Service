package com.youquiz.authentication.fixture

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.config.JwtConfiguration
import org.springframework.security.core.authority.SimpleGrantedAuthority

val SECRET_KEY = (1..100).map { ('a'..'z').random() }.joinToString("")
const val ACCESS_TOKEN_EXPIRE = 30L
const val REFRESH_TOKEN_EXPIRE = 120L
val AUTHORITIES = listOf(SimpleGrantedAuthority(ROLE.name))
const val INVALID_TOKEN = "test"
val JWT_AUTHENTICATION = DefaultJwtAuthentication(id = ID, authorities = AUTHORITIES)

val jwtProvider = JwtConfiguration().jwtProvider(
    secretKey = SECRET_KEY, accessTokenExpire = ACCESS_TOKEN_EXPIRE, refreshTokenExpire = REFRESH_TOKEN_EXPIRE
)