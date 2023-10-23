package com.quizit.authentication.service

import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.exception.InvalidAccessException
import com.quizit.authentication.exception.TokenNotFoundException
import com.quizit.authentication.fixture.ID
import com.quizit.authentication.fixture.createRefreshRequest
import com.quizit.authentication.fixture.createToken
import com.quizit.authentication.fixture.jwtProvider
import com.quizit.authentication.repository.TokenRepository
import com.quizit.authentication.util.empty
import com.quizit.authentication.util.getResult
import com.quizit.authentication.util.returns
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.kotlin.test.expectError

class AuthenticationServiceTest : BehaviorSpec() {
    private val tokenRepository = mockk<TokenRepository>()

    private val userClient = mockk<UserClient>()

    private val authenticationService =
        AuthenticationService(
            tokenRepository = tokenRepository,
            userClient = userClient,
            jwtProvider = jwtProvider,
        )

    init {
        Given("유저가 로그인 상태인 경우") {
            every { tokenRepository.findByUserId(any()) } returns createToken()
            every { tokenRepository.save(any()) } returns true
            every { tokenRepository.deleteByUserId(any()) } returns true

            When("로그아웃을 시도하면") {
                authenticationService.logout(ID)
                    .subscribe()

                Then("해당 유저의 리프레쉬 토큰이 삭제된다.") {
                    verify { tokenRepository.deleteByUserId(any()) }
                }
            }

            When("유효한 리프레쉬 토큰으로 로그인 유지를 시도하면") {
                val result = authenticationService.refresh(createRefreshRequest())
                    .getResult()

                Then("해당 유저에 대한 액세스 토큰과 리프레쉬 토큰이 발급된다.") {
                    result.expectSubscription()
                        .assertNext {
                            jwtProvider.getAuthentication(it.accessToken).id shouldBe ID
                            jwtProvider.getAuthentication(it.refreshToken).id shouldBe ID
                        }
                        .verifyComplete()
                }
            }

            When("유효하지 않은 리프레쉬 토큰으로 로그인 유지를 시도하면") {
                val result = authenticationService.refresh(createRefreshRequest(refreshToken = "invalid_token"))
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<InvalidAccessException>()
                        .verify()
                }
            }
        }

        Given("로그인 상태가 아닌 경우") {
            every { tokenRepository.findByUserId(any()) } returns empty()

            When("리프레쉬 토큰으로 로그인 유지를 시도하면") {
                val result = authenticationService.refresh(createRefreshRequest())
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<TokenNotFoundException>()
                        .verify()
                }
            }
        }
    }
}