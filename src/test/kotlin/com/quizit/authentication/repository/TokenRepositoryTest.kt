package com.quizit.authentication.repository

import com.quizit.authentication.config.RedisTestConfiguration
import com.quizit.authentication.fixture.ID
import com.quizit.authentication.fixture.REFRESH_TOKEN_EXPIRE
import com.quizit.authentication.fixture.createToken
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equality.shouldNotBeEqualToComparingFields
import io.kotest.matchers.nulls.shouldBeNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [RedisTestConfiguration::class])
class TokenRepositoryTest : ExpectSpec() {
    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

    private val tokenRepository by lazy {
        TokenRepository(
            redisTemplate = redisTemplate,
            expire = REFRESH_TOKEN_EXPIRE
        )
    }

    override suspend fun beforeContainer(testCase: TestCase) {
        redisTemplate.execute { it.serverCommands().flushAll() }.awaitSingle()
    }

    init {
        context("리프레쉬 토큰 조회") {
            val refreshToken = createToken().also { tokenRepository.save(it) }

            expect("특정 유저의 리프레쉬 토큰을 조회한다.") {
                tokenRepository.findByUserId(ID)!! shouldBeEqualToComparingFields refreshToken
            }
        }

        context("리프레쉬 토큰 수정") {
            val refreshToken = createToken().also { tokenRepository.save(it) }

            expect("특정 유저의 리프레쉬 토큰을 수정한다.") {
                createToken(content = "test").let { tokenRepository.save(it) }

                tokenRepository.findByUserId(ID)!! shouldNotBeEqualToComparingFields refreshToken
            }
        }

        context("리프레쉬 토큰 삭제") {
            createToken().let { tokenRepository.save(it) }

            expect("특정 유저의 리프레쉬 토큰을 삭제한다.") {
                tokenRepository.deleteByUserId(ID)

                tokenRepository.findByUserId(ID).shouldBeNull()
            }
        }
    }
}