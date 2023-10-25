package com.quizit.authentication.handler

import com.quizit.authentication.exception.InvalidTokenException
import com.quizit.authentication.global.annotation.Handler
import com.quizit.authentication.global.util.authentication
import com.quizit.authentication.service.AuthenticationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import java.time.Duration

@Handler
class AuthenticationHandler(
    private val authenticationService: AuthenticationService,
    @Value("\${url.frontend}")
    private val frontendUrl: String,
    @Value("\${jwt.refreshTokenExpire}")
    private val expire: Long,
) {
    fun logout(request: ServerRequest): Mono<ServerResponse> =
        request.authentication()
            .flatMap {
                ServerResponse.ok()
                    .body(authenticationService.logout(it.id))
            }

    fun refresh(request: ServerRequest): Mono<ServerResponse> =
        request.cookies()
            .getFirst("refreshToken")
            ?.value
            ?.let {
                authenticationService.refresh(it)
                    .flatMap { (response, refreshToken) ->
                        ServerResponse.ok()
                            .cookie(
                                ResponseCookie.from("refreshToken", refreshToken)
                                    .path("$frontendUrl/")
                                    .httpOnly(true)
                                    .secure(true)
                                    .maxAge(Duration.ofMinutes(expire))
                                    .build()
                            )
                            .bodyValue(response)
                    }
            } ?: Mono.error(InvalidTokenException())
}