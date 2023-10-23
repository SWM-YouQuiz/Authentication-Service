package com.quizit.authentication.adapter.producer

import com.quizit.authentication.dto.event.RevokeOAuthEvent
import com.quizit.authentication.global.annotation.Producer
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Mono

@Producer
class AuthenticationProducer(
    private val kafkaTemplate: ReactiveKafkaProducerTemplate<String, Any>,
) {
    fun revokeOAuth(event: RevokeOAuthEvent): Mono<Void> =
        kafkaTemplate.send("revoke-oauth", event)
            .then()
}