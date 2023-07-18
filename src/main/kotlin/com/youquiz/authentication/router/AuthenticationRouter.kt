package com.youquiz.authentication.router

import com.youquiz.authentication.handler.AuthenticationHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AuthenticationRouter {
    @Bean
    fun authenticationRoutes(handler: AuthenticationHandler): RouterFunction<ServerResponse> =
        coRouter {
            "/auth".nest {
                POST("/login", handler::login)
                GET("/logout", handler::logout)
            }
        }
}