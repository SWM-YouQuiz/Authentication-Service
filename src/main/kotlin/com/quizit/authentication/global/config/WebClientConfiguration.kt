package com.quizit.authentication.global.config

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.quizit.authentication.global.util.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    private val logger = getLogger()

    @Bean
    fun webClient(): WebClient =
        WebClient.builder()
            .filter { request, next ->
                ReactiveSecurityContextHolder.getContext()
                    .map { it.authentication }
                    .map {
                        ClientRequest.from(request)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer ${(it as DefaultJwtAuthentication).token}")
                            .build()
                    }
                    .defaultIfEmpty(request)
                    .flatMap {
                        logger.info { "HTTP ${request.method()} ${request.url().path}" }
                        next.exchange(it)
                            .doOnNext { logger.info { "HTTP ${it.statusCode()}" } }
                    }
            }
            .build()
}