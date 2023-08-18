package com.quizit.authentication.service

import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.exception.InvalidAccessException
import com.quizit.authentication.exception.PasswordNotMatchException
import com.quizit.authentication.exception.TokenNotFoundException
import com.quizit.authentication.exception.UserNotFoundException
import com.quizit.authentication.fixture.*
import com.quizit.authentication.repository.TokenRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthenticationServiceTest : BehaviorSpec() {
    private val tokenRepository = mockk<TokenRepository>()

    private val userClient = mockk<UserClient>()

    private val authenticationService =
        AuthenticationService(
            tokenRepository = tokenRepository,
            userClient = userClient,
            jwtProvider = jwtProvider,
            passwordEncoder = BCryptPasswordEncoder()
        )

    init {
        Given("해당 아이디를 가진 유저가 존재하고 비밀번호가 일치하는 경우") {
            coEvery { userClient.getUserByUsername(any()) } returns createFindUserByUsernameResponse()
            coEvery { userClient.getPasswordByUsername(any()) } returns createGetUserPasswordByUsernameResponse()
            coEvery { tokenRepository.save(any()) } returns true

            When("로그인을 시도하면") {
                val loginResponse = authenticationService.login(createLoginRequest())

                Then("해당 유저에 대한 액세스 토큰과 리프레쉬 토큰이 발급된다.") {
                    jwtProvider.getAuthentication(loginResponse.accessToken).id shouldBe ID
                    jwtProvider.getAuthentication(loginResponse.refreshToken).id shouldBe ID
                }
            }
        }

        Given("해당 아이디를 가진 유저가 존재하지만 비밀번호가 일치하지 않는 경우") {
            coEvery { userClient.getUserByUsername(any()) } returns createFindUserByUsernameResponse()
            coEvery { userClient.getPasswordByUsername(any()) } returns createGetUserPasswordByUsernameResponse()
            coEvery { tokenRepository.save(any()) } returns false

            When("로그인을 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<PasswordNotMatchException> {
                        authenticationService.login(createLoginRequest(password = INVALID_PASSWORD))
                    }
                }
            }
        }

        Given("해당 아이디를 가진 유저가 존재하지 않는 경우") {
            coEvery {
                userClient.getUserByUsername(
                    any()
                )
            } throws UserNotFoundException()
            coEvery { userClient.getPasswordByUsername(any()) } throws UserNotFoundException()
            coEvery { tokenRepository.save(any()) } returns false

            When("로그인을 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        authenticationService.login(createLoginRequest(username = INVALID_USERNAME))
                    }
                }
            }
        }

        Given("유저가 로그인 상태인 경우") {
            coEvery { tokenRepository.findByUserId(any()) } returns createToken()
            coEvery { tokenRepository.save(any()) } returns true
            coEvery { tokenRepository.deleteByUserId(any()) } returns true

            When("로그아웃을 시도하면") {
                authenticationService.logout(ID)

                Then("해당 유저의 리프레쉬 토큰이 삭제된다.") {
                    coVerify { tokenRepository.deleteByUserId(any()) }
                }
            }

            When("유효한 리프레쉬 토큰으로 로그인 유지를 시도하면") {
                val refreshResponse = authenticationService.refresh(createRefreshRequest())

                Then("해당 유저에 대한 액세스 토큰과 리프레쉬 토큰이 발급된다.") {
                    jwtProvider.getAuthentication(refreshResponse.accessToken).id shouldBe ID
                    jwtProvider.getAuthentication(refreshResponse.refreshToken).id shouldBe ID
                }
            }

            When("유효하지 않은 리프레쉬 토큰으로 로그인 유지를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<InvalidAccessException> {
                        authenticationService.refresh(createRefreshRequest(refreshToken = INVALID_TOKEN))
                    }
                }
            }

        }

        Given("로그인 상태가 아닌 경우") {
            coEvery { tokenRepository.findByUserId(any()) } returns null

            When("리프레쉬 토큰으로 로그인 유지를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<TokenNotFoundException> {
                        authenticationService.refresh(createRefreshRequest())
                    }
                }
            }
        }
    }
}