package com.quizit.authentication.service

import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.exception.InvalidAccessException
import com.quizit.authentication.exception.PasswordNotMatchException
import com.quizit.authentication.exception.TokenNotFoundException
import com.quizit.authentication.exception.UserNotFoundException
import com.quizit.authentication.fixture.*
import com.quizit.authentication.repository.TokenRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError
import reactor.test.StepVerifier

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
        Given("해당 아이디를 가진 유저가 존재하고 비밀번호가 일치하는 경우") {
            every { userClient.getUserByUsername(any()) } returns Mono.just(createUserResponse())
            every { userClient.matchPassword(any(), any()) } returns Mono.just(createMatchPasswordResponse())
            every { tokenRepository.save(any()) } returns Mono.just(true)

            When("로그인을 시도하면") {
                val result = StepVerifier.create(authenticationService.login(createLoginRequest()))

                Then("해당 유저에 대한 액세스 토큰과 리프레쉬 토큰이 발급된다.") {
                    result.expectSubscription()
                        .assertNext {
                            jwtProvider.getAuthentication(it.accessToken).id shouldBe ID
                            jwtProvider.getAuthentication(it.refreshToken).id shouldBe ID
                        }
                        .verifyComplete()
                }
            }
        }

        Given("해당 아이디를 가진 유저가 존재하지만 비밀번호가 일치하지 않는 경우") {
            every { userClient.getUserByUsername(any()) } returns Mono.just(createUserResponse())
            every { userClient.matchPassword(any(), any()) } returns Mono.just(createMatchPasswordResponse(false))
            every { tokenRepository.save(any()) } returns Mono.just(false)

            When("로그인을 시도하면") {
                val result =
                    StepVerifier.create(authenticationService.login(createLoginRequest(password = INVALID_PASSWORD)))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PasswordNotMatchException>()
                        .verify()
                }
            }
        }

        Given("해당 아이디를 가진 유저가 존재하지 않는 경우") {
            every { userClient.getUserByUsername(any()) } returns Mono.error(UserNotFoundException())
            every { userClient.matchPassword(any(), any()) } returns Mono.error(UserNotFoundException())
            every { tokenRepository.save(any()) } returns Mono.just(false)

            When("로그인을 시도하면") {
                val result =
                    StepVerifier.create(authenticationService.login(createLoginRequest(username = INVALID_USERNAME)))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }
        }

        Given("유저가 로그인 상태인 경우") {
            every { tokenRepository.findByUserId(any()) } returns Mono.just(createToken())
            every { tokenRepository.save(any()) } returns Mono.just(true)
            every { tokenRepository.deleteByUserId(any()) } returns Mono.just(true)

            When("로그아웃을 시도하면") {
                authenticationService.logout(ID)
                    .subscribe()

                Then("해당 유저의 리프레쉬 토큰이 삭제된다.") {
                    verify { tokenRepository.deleteByUserId(any()) }
                }
            }

            When("유효한 리프레쉬 토큰으로 로그인 유지를 시도하면") {
                val result = StepVerifier.create(authenticationService.refresh(createRefreshRequest()))

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
                val result = StepVerifier.create(
                    authenticationService.refresh(createRefreshRequest(refreshToken = INVALID_TOKEN))
                )

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<InvalidAccessException>()
                        .verify()
                }
            }

        }

        Given("로그인 상태가 아닌 경우") {
            every { tokenRepository.findByUserId(any()) } returns Mono.empty()

            When("리프레쉬 토큰으로 로그인 유지를 시도하면") {
                val result = StepVerifier.create(authenticationService.refresh(createRefreshRequest()))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<TokenNotFoundException>()
                        .verify()
                }
            }
        }
    }
}