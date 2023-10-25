package com.quizit.authentication.controller

import com.ninjasquad.springmockk.MockkBean
import com.quizit.authentication.dto.response.RefreshResponse
import com.quizit.authentication.exception.InvalidAccessException
import com.quizit.authentication.exception.InvalidTokenException
import com.quizit.authentication.exception.TokenNotFoundException
import com.quizit.authentication.fixture.REFRESH_TOKEN
import com.quizit.authentication.fixture.createRefreshResponse
import com.quizit.authentication.global.dto.ErrorResponse
import com.quizit.authentication.handler.AuthenticationHandler
import com.quizit.authentication.router.AuthenticationRouter
import com.quizit.authentication.service.AuthenticationService
import com.quizit.authentication.util.*
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest(AuthenticationHandler::class, AuthenticationRouter::class)
class AuthenticationControllerTest : ControllerTest() {
    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    private val refreshResponseFields = listOf(
        "accessToken" desc "액세스 토큰",
    )

    init {
        describe("logout()은") {
            context("요청을 보낸 유저가 로그인 상태인 경우") {
                every { authenticationService.logout(any()) } returns empty()
                withMockUser()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .get()
                        .uri("/auth/logout")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .document("로그아웃 성공(200)")
                }
            }
        }

        describe("refresh()는") {
            context("요청을 보낸 유저가 로그인 상태인 경우") {
                every { authenticationService.refresh(any()) } returns Pair(createRefreshResponse(), REFRESH_TOKEN)
                withMockUser()

                it("상태 코드 200과 refreshResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/refresh")
                        .cookie("refreshToken", REFRESH_TOKEN)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<RefreshResponse>()
                        .document(
                            "토큰 재발급 성공(200)",
                            requestCookies("refreshToken" cookieDesc "리프레쉬 토큰"),
                            responseFields(refreshResponseFields)
                        )
                }
            }

            context("요청을 보낸 유저의 리프레쉬 토큰이 저장소에 존재하지 않는 경우") {
                every { authenticationService.refresh(any()) } throws TokenNotFoundException()
                withMockUser()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/refresh")
                        .cookie("refreshToken", REFRESH_TOKEN)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody<ErrorResponse>()
                        .document(
                            "토큰 리프레쉬 실패(404)",
                            requestCookies("refreshToken" cookieDesc "리프레쉬 토큰"),
                            responseFields(errorResponseFields)
                        )
                }
            }

            context("토큰이 없거나 토큰이 유효하지 않은 경우") {
                every { authenticationService.refresh(any()) } throws InvalidTokenException()
                withMockUser()

                it("상태 코드 403과 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/refresh")
                        .cookie("refreshToken", REFRESH_TOKEN)
                        .exchange()
                        .expectStatus()
                        .isForbidden
                        .expectBody<ErrorResponse>()
                        .document(
                            "토큰 리프레쉬 실패(403 - 1)",
                            requestCookies("refreshToken" cookieDesc "리프레쉬 토큰"),
                            responseFields(errorResponseFields)
                        )
                }
            }

            context("유저의 리프레쉬 토큰이 저장소에 있는 리프레쉬 토큰과 일치하지 않는 경우") {
                every { authenticationService.refresh(any()) } throws InvalidAccessException()
                withMockUser()

                it("상태 코드 403과 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/refresh")
                        .cookie("refreshToken", REFRESH_TOKEN)
                        .exchange()
                        .expectStatus()
                        .isForbidden
                        .expectBody<ErrorResponse>()
                        .document(
                            "토큰 리프레쉬 실패(403 - 2)",
                            requestCookies("refreshToken" cookieDesc "리프레쉬 토큰"),
                            responseFields(errorResponseFields)
                        )
                }
            }
        }
    }
}