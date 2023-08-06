package com.quizit.authentication.config

import com.quizit.authentication.fixture.jwtProvider
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
class SecurityTestConfiguration {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        com.quizit.authentication.global.config.SecurityConfiguration().filterChain(http, jwtProvider)
}