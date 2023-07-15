package com.youquiz.authentication.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.youquiz.authentication.dto.LoginRequest
import com.youquiz.authentication.dto.LoginResponse
import com.youquiz.authentication.exception.PasswordNotMatchException
import com.youquiz.authentication.exception.UserNotFoundException
import com.youquiz.authentication.fixture.JWT_AUTHENTICATION
import com.youquiz.authentication.fixture.PASSWORD
import com.youquiz.authentication.fixture.USERNAME
import com.youquiz.authentication.fixture.jwtProvider
import com.youquiz.authentication.global.dto.ErrorResponse
import com.youquiz.authentication.handler.AuthenticationHandler
import com.youquiz.authentication.router.AuthenticationRouter
import com.youquiz.authentication.service.AuthenticationService
import com.youquiz.authentication.util.BaseControllerTest
import com.youquiz.authentication.util.desc
import com.youquiz.authentication.util.errorResponseFields
import com.youquiz.authentication.util.withMockUser
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

@WebFluxTest(AuthenticationHandler::class, AuthenticationRouter::class)
class AuthenticationControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    private val loginRequest = LoginRequest(
        username = USERNAME,
        password = PASSWORD
    )

    private val loginResponse = LoginResponse(
        accessToken = jwtProvider.createAccessToken(JWT_AUTHENTICATION),
        refreshToken = jwtProvider.createRefreshToken(JWT_AUTHENTICATION)
    )

    init {
        describe("login()은") {
            context("해당 아이디를 가진 유저가 존재하고 비밀번호가 일치하는 경우") {
                coEvery { authenticationService.login(any()) } returns loginResponse
                withMockUser()

                it("상태 코드 200과 LoginResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/login")
                        .bodyValue(loginRequest)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(LoginResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "로그인 성공",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                    "username" desc "아이디",
                                    "password" desc "패스워드"
                                ),
                                responseFields(
                                    "accessToken" desc "액세스 토큰",
                                    "refreshToken" desc "리프레쉬 토큰"
                                )
                            )
                        )
                }
            }

            context("해당 아이디를 가진 유저가 존재하고 비밀번호가 일치하지 않는 경우") {
                coEvery { authenticationService.login(any()) } throws PasswordNotMatchException()
                withMockUser()

                it("상태 코드 400과 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/login")
                        .bodyValue(loginRequest)
                        .exchange()
                        .expectStatus()
                        .isBadRequest
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "로그인 실패(400)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                    "username" desc "아이디",
                                    "password" desc "패스워드"
                                ),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }

            context("요청으로 주어진 아이디가 존재하지 않는 경우") {
                coEvery { authenticationService.login(any()) } throws UserNotFoundException()
                withMockUser()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/login")
                        .bodyValue(loginRequest)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "로그인 실패(404)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                    "username" desc "아이디",
                                    "password" desc "패스워드"
                                ),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }
    }
}