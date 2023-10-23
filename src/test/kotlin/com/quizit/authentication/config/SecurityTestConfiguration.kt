package com.quizit.authentication.config

import com.github.jwt.authentication.JwtAuthenticationFilter
import com.quizit.authentication.fixture.jwtProvider
import com.quizit.authentication.global.config.SecurityConfiguration
import com.quizit.authentication.global.oauth.OAuth2LoginSuccessHandler
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
class SecurityTestConfiguration {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        SecurityConfiguration()
            .filterChain(http, jwtAuthenticationFilter(), mockk<OAuth2LoginSuccessHandler>())

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter =
        JwtAuthenticationFilter(jwtProvider)
}