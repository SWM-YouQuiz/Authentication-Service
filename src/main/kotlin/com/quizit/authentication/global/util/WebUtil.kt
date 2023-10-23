package com.quizit.authentication.global.util

import com.github.jwt.authentication.DefaultJwtAuthentication
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.cast

fun RouterFunctionDsl.queryParams(vararg names: String): RequestPredicate =
    names.map { queryParam(it) { true } }.reduce { total, next -> total and next }

inline fun <reified T> ServerRequest.queryParamNotNull(name: String): T =
    this.queryParamOrNull(name)!!.run {
        when (T::class) {
            Int::class -> toInt()
            Boolean::class -> toBoolean()
            else -> this
        } as T
    }

fun ServerRequest.authentication(): Mono<DefaultJwtAuthentication> =
    principal()
        .cast<DefaultJwtAuthentication>()