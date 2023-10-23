package com.quizit.authentication.repository

import com.quizit.authentication.config.RedisTestConfiguration
import com.quizit.authentication.fixture.ID
import com.quizit.authentication.fixture.REFRESH_TOKEN_EXPIRE
import com.quizit.authentication.fixture.createToken
import com.quizit.authentication.fixture.createUserResponse
import com.quizit.authentication.util.getResult
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.nulls.shouldBeNull
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
        redisTemplate.execute {
            it.serverCommands()
                .flushAll()
        }.subscribe()
    }

    init {
        context("리프레쉬 토큰 조회") {
            createUserResponse() // Code Coverage

            val refreshToken = createToken()
                .also {
                    tokenRepository.save(it)
                        .subscribe()
                }

            expect("특정 유저의 리프레쉬 토큰을 조회한다.") {
                val result = tokenRepository.findByUserId(ID)
                    .getResult()

                result.expectSubscription()
                    .assertNext { it shouldBeEqual refreshToken }
                    .verifyComplete()
            }
        }

        context("리프레쉬 토큰 수정") {
            val refreshToken = createToken()
                .also {
                    tokenRepository.save(it)
                        .subscribe()
                }

            expect("특정 유저의 리프레쉬 토큰을 수정한다.") {
                val result = tokenRepository.save(createToken(content = "updated_content"))
                    .getResult()

                result.expectSubscription()
                    .assertNext {
                        tokenRepository.findByUserId(ID)
                            .subscribe { it shouldNotBeEqual refreshToken }
                    }
                    .verifyComplete()
            }
        }

        context("리프레쉬 토큰 삭제") {
            createToken()
                .also {
                    tokenRepository.save(it)
                        .subscribe()
                }

            expect("특정 유저의 리프레쉬 토큰을 삭제한다.") {
                val result = tokenRepository.deleteByUserId(ID)
                    .getResult()

                result.expectSubscription()
                    .assertNext {
                        tokenRepository.findByUserId(ID)
                            .subscribe { it.shouldBeNull() }
                    }
                    .verifyComplete()
            }
        }
    }
}