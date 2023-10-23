package com.quizit.authentication.router

import com.quizit.authentication.global.annotation.Router
import com.quizit.authentication.global.util.queryParams
import com.quizit.authentication.handler.KakaoOAuth2Handler
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class KakaoOAuth2Router {
    @Bean
    fun kakaoOAuth2Routes(handler: KakaoOAuth2Handler): RouterFunction<ServerResponse> =
        router {
            "/oauth2".nest {
                GET("/revoke/kakao", handler::revoke)
                POST("/redirect/kakao/revoke", queryParams("code", "state"), handler::revokeRedirect)
            }
        }
}