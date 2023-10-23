package com.quizit.authentication.global.config

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions

@Configuration
class ProducerConfiguration(
    private val properties: KafkaProperties
) {
    @Bean
    fun reactiveKafkaProducerTemplate(): ReactiveKafkaProducerTemplate<String, Any> =
        ReactiveKafkaProducerTemplate(
            SenderOptions.create<String, Any>(properties.buildProducerProperties())
                .withValueSerializer(JsonSerializer())
        )
}

@Configuration
class ConsumerConfiguration(
    private val properties: KafkaProperties
) {
    private inline fun <reified T> createReceiverOptions(topic: String): ReceiverOptions<String, T> =
        ReceiverOptions.create<String, T>(
            properties.run {
                consumer.groupId = "$topic-group"
                buildConsumerProperties()
            })
            .subscription(listOf(topic))
            .withValueDeserializer(JsonDeserializer<T>(T::class.java, false))
}