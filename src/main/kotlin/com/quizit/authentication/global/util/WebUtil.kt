package com.quizit.authentication.global.util

import com.github.jwt.authentication.DefaultJwtAuthentication
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.cast

fun ServerRequest.authentication(): Mono<DefaultJwtAuthentication> =
    principal()
        .cast<DefaultJwtAuthentication>()