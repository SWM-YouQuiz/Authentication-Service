package com.quizit.authentication.global.config

import com.github.jwt.authentication.JwtAuthenticationFilter
import com.github.jwt.core.JwtProvider
import com.quizit.authentication.domain.enum.Role
import com.quizit.authentication.global.oauth.OAuth2LoginSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@EnableWebFluxSecurity
@Configuration
class SecurityConfiguration {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
        oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler
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
                        "/actuator/**",
                        "/oauth2/**",
                        "/login/**",
                        "/auth/refresh"
                    )
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }
            oauth2Login { it.authenticationSuccessHandler(oAuth2LoginSuccessHandler) }
            addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
            build()
        }

    @Bean
    fun jwtAuthenticationFilter(jwtProvider: JwtProvider): JwtAuthenticationFilter =
        JwtAuthenticationFilter(jwtProvider)
}