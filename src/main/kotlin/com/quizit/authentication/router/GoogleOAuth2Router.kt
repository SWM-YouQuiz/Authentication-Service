package com.quizit.authentication.router

import com.quizit.authentication.global.annotation.Router
import com.quizit.authentication.handler.GoogleOAuth2Handler
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class GoogleOAuth2Router {
    @Bean
    fun googleOAuth2Routes(handler: GoogleOAuth2Handler): RouterFunction<ServerResponse> =
        router {
            "/oauth2".nest {
                GET("/revoke/google", handler::revoke)
                POST("/redirect/google/revoke", handler::revokeRedirect)
            }
        }
}