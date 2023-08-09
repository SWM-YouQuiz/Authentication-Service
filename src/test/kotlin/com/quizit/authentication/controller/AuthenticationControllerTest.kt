package com.quizit.authentication.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.quizit.authentication.fixture.createLoginRequest
import com.quizit.authentication.fixture.createLoginResponse
import com.quizit.authentication.fixture.createRefreshRequest
import com.quizit.authentication.fixture.createRefreshResponse
import com.quizit.authentication.global.dto.ErrorResponse
import com.quizit.authentication.handler.AuthenticationHandler
import com.quizit.authentication.router.AuthenticationRouter
import com.quizit.authentication.service.AuthenticationService
import com.quizit.authentication.util.BaseControllerTest
import com.quizit.authentication.util.desc
import com.quizit.authentication.util.errorResponseFields
import com.quizit.authentication.util.withMockUser
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

@WebFluxTest(AuthenticationHandler::class, AuthenticationRouter::class)
class AuthenticationControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    private val loginRequestFields = listOf(
        "username" desc "아이디",
        "password" desc "패스워드"
    )

    private val refreshRequestFields = listOf(
        "userId" desc "유저 식별자",
        "refreshToken" desc "리프레쉬 토큰"
    )

    private val loginResponseFields = listOf(
        "accessToken" desc "액세스 토큰",
        "refreshToken" desc "리프레쉬 토큰"
    )

    private val refreshResponseFields = listOf(
        "accessToken" desc "액세스 토큰",
        "refreshToken" desc "리프레쉬 토큰"
    )

    init {
        describe("login()은") {
            context("해당 아이디를 가진 유저가 존재하고 비밀번호가 일치하는 경우") {
                coEvery { authenticationService.login(any()) } returns createLoginResponse()

                it("상태 코드 200과 LoginResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/login")
                        .bodyValue(createLoginRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(com.quizit.authentication.dto.response.LoginResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "로그인 성공(200)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(loginRequestFields),
                                responseFields(loginResponseFields)
                            )
                        )
                }
            }

            context("해당 아이디를 가진 유저가 존재하고 비밀번호가 일치하지 않는 경우") {
                coEvery {
                    authenticationService.login(
                        any()
                    )
                } throws com.quizit.authentication.exception.PasswordNotMatchException()

                it("상태 코드 400과 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/login")
                        .bodyValue(createLoginRequest())
                        .exchange()
                        .expectStatus()
                        .isBadRequest
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "로그인 실패(400)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(loginRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }

            context("요청으로 주어진 아이디가 존재하지 않는 경우") {
                coEvery {
                    authenticationService.login(
                        any()
                    )
                } throws com.quizit.authentication.exception.UserNotFoundException()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/login")
                        .bodyValue(createLoginRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "로그인 실패(404)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(loginRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }


        describe("logout()은") {
            context("요청을 보낸 유저가 로그인 상태인 경우") {
                coEvery { authenticationService.logout(any()) } just Runs
                withMockUser()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .get()
                        .uri("/auth/logout")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .consumeWith(WebTestClientRestDocumentationWrapper.document("로그아웃 성공(200)"))
                }
            }
        }

        describe("refresh()는") {
            context("요청을 보낸 유저가 로그인 상태인 경우") {
                coEvery { authenticationService.refresh(any()) } returns createRefreshResponse()

                it("상태 코드 200과 refreshResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/refresh")
                        .bodyValue(createRefreshRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(com.quizit.authentication.dto.response.RefreshResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "토큰 재발급 성공(200)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(refreshRequestFields),
                                responseFields(refreshResponseFields)
                            )
                        )
                }
            }

            context("요청을 보낸 유저의 리프레쉬 토큰이 저장소에 존재하지 않는 경우") {
                coEvery {
                    authenticationService.refresh(
                        any()
                    )
                } throws com.quizit.authentication.exception.TokenNotFoundException()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/refresh")
                        .bodyValue(createRefreshRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "토큰 리프레쉬 실패(404)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(refreshRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }

            context("요청을 보낸 유저의 리프레쉬 토큰이 저장소에 있는 리프레쉬 토큰과 일치하지 않는 경우") {
                coEvery {
                    authenticationService.refresh(
                        any()
                    )
                } throws com.quizit.authentication.exception.InvalidAccessException()

                it("상태 코드 403과 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/refresh")
                        .bodyValue(createRefreshRequest())
                        .exchange()
                        .expectStatus()
                        .isForbidden
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "토큰 리프레쉬 실패(403)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(refreshRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }
    }
}