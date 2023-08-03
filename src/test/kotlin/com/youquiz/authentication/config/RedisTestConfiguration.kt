package com.youquiz.authentication.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext

@TestConfiguration
class RedisTestConfiguration {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory =
        LettuceConnectionFactory(RedisStandaloneConfiguration("localhost", 6379))

    @Bean
    fun reactiveRedisTemplate(): ReactiveRedisTemplate<String, String> =
        ReactiveRedisTemplate(redisConnectionFactory(), RedisSerializationContext.string())
}