package com.quizit.authentication.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import redis.embedded.RedisServer

@TestConfiguration
class RedisTestConfiguration {
    private val redisServer: RedisServer by lazy { RedisServer(6380) }

    @PostConstruct
    fun startRedis() {
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory =
        LettuceConnectionFactory(RedisStandaloneConfiguration("localhost", 6380))

    @Bean
    fun reactiveRedisTemplate(): ReactiveRedisTemplate<String, String> =
        ReactiveRedisTemplate(redisConnectionFactory(), RedisSerializationContext.string())
}