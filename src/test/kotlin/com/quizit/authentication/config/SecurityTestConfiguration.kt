package com.quizit.authentication.config

import com.github.jwt.authentication.JwtAuthenticationFilter
import com.quizit.authentication.domain.enum.Role
import com.quizit.authentication.fixture.jwtProvider
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@TestConfiguration
class SecurityTestConfiguration {
    @Bean
    fun filterChain(
        http: ServerHttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityWebFilterChain =
        with(http) {
            csrf { it.disable() }
            formLogin { it.disable() }
            httpBasic { it.disable() }
            logout { it.disable() }
            requestCache { it.disable() }
            securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            exceptionHandling { it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) }
            authorizeExchange {
                it.pathMatchers("/api/auth/admin/**")
                    .hasAuthority(Role.ADMIN.name)
                    .pathMatchers(
                        "/login/**",
                        "/auth/refresh"
                    )
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }
            addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
            build()
        }

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter =
        JwtAuthenticationFilter(jwtProvider)
}