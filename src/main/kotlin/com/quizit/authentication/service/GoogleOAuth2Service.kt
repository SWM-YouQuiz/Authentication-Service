package com.quizit.authentication.service

import com.quizit.authentication.adapter.client.GoogleClient
import com.quizit.authentication.adapter.producer.AuthenticationProducer
import com.quizit.authentication.dto.event.RevokeOAuthEvent
import com.quizit.authentication.global.util.component1
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.net.URI

@Service
class GoogleOAuth2Service(
    private val googleClient: GoogleClient,
    private val authenticationProducer: AuthenticationProducer,
    @Value("\${url.frontend}")
    private val frontendUrl: String
) {
    fun revokeRedirect(code: String): Mono<ServerResponse> =
        googleClient.getTokenResponseByCodeAndRedirectUri(code, "$frontendUrl/api/auth/oauth2/redirect/google/revoke")
            .map { it["access_token"] as String }
            .flatMap {
                Mono.zip(
                    googleClient.getOAuth2UserByToken(it)
                        .subscribeOn(Schedulers.boundedElastic()),
                    googleClient.revokeByToken(it)
                        .subscribeOn(Schedulers.boundedElastic()),
                )
            }
            .flatMap { (oAuth2User) ->
                oAuth2User.run {
                    authenticationProducer.revokeOAuth(
                        RevokeOAuthEvent(
                            email = email,
                            provider = provider
                        )
                    )
                }
            }
            .then(Mono.defer {
                ServerResponse.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl))
                    .build()
            })
}