package com.quizit.authentication.global.util

import mu.KLogger
import mu.KotlinLogging
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import kotlin.reflect.jvm.jvmName

fun logFilter(request: ServerRequest, handler: (ServerRequest) -> Mono<ServerResponse>): Mono<ServerResponse> =
    with(request) {
        val logger = getLogger()

        bodyToMono<String>()
            .defaultIfEmpty("")
            .flatMap {
                logger.info {
                    "HTTP ${method()} ${requestPath()} ${
                        it.replace(" ", "").replace("\n", "").replace(",", ", ").trim()
                    }"
                }
                handler(
                    ServerRequest.from(request)
                        .body(it)
                        .build()
                ).doOnNext { logger.info { "HTTP ${it.statusCode()}" } }
            }
    }

inline fun <reified T> T.getLogger(): KLogger = KotlinLogging.logger(T::class.jvmName)