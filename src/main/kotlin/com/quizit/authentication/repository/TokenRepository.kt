package com.quizit.authentication.repository

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class TokenRepository(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    @Value("\${jwt.refreshTokenExpire}")
    private val expire: Long
) {
    suspend fun findByUserId(userId: String): com.quizit.authentication.domain.Token? =
        redisTemplate.opsForValue()
            .get(getKey(userId))
            .awaitSingleOrNull()?.let {
                com.quizit.authentication.domain.Token(
                    userId = userId,
                    content = it
                )
            }

    suspend fun save(token: com.quizit.authentication.domain.Token): Boolean =
        with(token) {
            redisTemplate.opsForValue()
                .set(getKey(userId), content, Duration.ofMinutes(expire))
                .awaitSingle()
        }

    suspend fun deleteByUserId(userId: String): Boolean =
        redisTemplate.opsForValue()
            .delete(getKey(userId))
            .awaitSingle()

    private fun getKey(id: String): String = "refreshToken:$id"
}