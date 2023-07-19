package com.youquiz.authentication.service

import com.youquiz.authentication.adapter.client.UserClient
import com.youquiz.authentication.dto.LoginRequest
import com.youquiz.authentication.dto.RefreshRequest
import com.youquiz.authentication.exception.InvalidAccessException
import com.youquiz.authentication.exception.PasswordNotMatchException
import com.youquiz.authentication.exception.TokenNotFoundException
import com.youquiz.authentication.exception.UserNotFoundException
import com.youquiz.authentication.fixture.*
import com.youquiz.authentication.repository.TokenRepository
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
            val user = createUser().also {
                coEvery { userClient.findByUsername(any()) } returns it
            }

            When("로그인을 시도하면") {
                val loginResponse =
                    authenticationService.login(LoginRequest(username = user.username, password = PASSWORD))

                Then("해당 유저에 대한 액세스 토큰과 리프레쉬 토큰이 발급된다.") {
                    jwtProvider.getAuthentication(loginResponse.accessToken).id shouldBe user.id
                    jwtProvider.getAuthentication(loginResponse.refreshToken).id shouldBe user.id
                }
            }
        }

        Given("해당 아이디를 가진 유저가 존재하지만 비밀번호가 일치하지 않는 경우") {
            val user = createUser().also {
                coEvery { userClient.findByUsername(any()) } returns it
            }

            When("로그인을 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<PasswordNotMatchException> {
                        authenticationService.login(LoginRequest(username = user.username, password = INVALID_PASSWORD))
                    }
                }
            }
        }

        Given("해당 아이디를 가진 유저가 존재하지 않는 경우") {
            coEvery { userClient.findByUsername(any()) } throws UserNotFoundException()

            When("로그인을 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        authenticationService.login(LoginRequest(username = INVALID_USERNAME, password = PASSWORD))
                    }
                }
            }
        }

        Given("유저가 로그인 상태인 경우") {
            val user = createUser()

            coEvery { tokenRepository.findByUserId(any()) } returns createToken()
            coEvery { tokenRepository.save(any()) } returns true
            coEvery { tokenRepository.deleteByUserId(any()) } returns true

            When("로그아웃을 시도하면") {
                authenticationService.logout(user.id)

                Then("해당 유저의 리프레쉬 토큰이 삭제된다.") {
                    coVerify { tokenRepository.deleteByUserId(any()) }
                }
            }

            When("유효한 리프레쉬 토큰으로 로그인 유지를 시도하면") {
                val refreshResponse = authenticationService.refresh(
                    RefreshRequest(
                        userId = user.id,
                        refreshToken = jwtProvider.createRefreshToken(createJwtAuthentication())
                    )
                )

                Then("해당 유저에 대한 액세스 토큰과 리프레쉬 토큰이 발급된다.") {
                    jwtProvider.getAuthentication(refreshResponse.accessToken).id shouldBe user.id
                    jwtProvider.getAuthentication(refreshResponse.refreshToken).id shouldBe user.id
                }
            }

            When("유효하지 않은 리프레쉬 토큰으로 로그인 유지를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<InvalidAccessException> {
                        authenticationService.refresh(
                            RefreshRequest(
                                userId = user.id,
                                refreshToken = INVALID_TOKEN
                            )
                        )
                    }
                }
            }

        }

        Given("로그인 상태가 아닌 경우") {
            coEvery { tokenRepository.findByUserId(any()) } returns null

            When("리프레쉬 토큰으로 로그인 유지를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<TokenNotFoundException> {
                        authenticationService.refresh(
                            RefreshRequest(
                                userId = ID,
                                refreshToken = jwtProvider.createRefreshToken(createJwtAuthentication())
                            )
                        )
                    }
                }
            }
        }
    }
}