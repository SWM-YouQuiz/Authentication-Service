package com.youquiz.authentication.repository

import com.youquiz.authentication.domain.Token
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
    suspend fun findByUserId(userId: Long): Token? =
        redisTemplate.opsForValue()
            .get(getKey(userId))
            .awaitSingleOrNull()?.let {
                Token(
                    userId = userId,
                    content = it
                )
            }

    suspend fun save(token: Token): Boolean =
        with(token) {
            redisTemplate.run {
                val key = getKey(userId)

                opsForValue().set(key, content, Duration.ofMinutes(expire)).awaitSingle()
            }
        }

    suspend fun deleteByUserId(userId: Long): Boolean =
        redisTemplate.opsForValue()
            .delete(getKey(userId))
            .awaitSingle()

    private fun getKey(id: Long): String = "refreshToken:$id"
}