package com.quizit.authentication.util

import com.quizit.authentication.domain.enum.Role
import com.quizit.authentication.fixture.createJwtAuthentication
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

infix fun String.desc(description: String): FieldDescriptor =
    fieldWithPath(this)
        .description(description)

infix fun String.paramDesc(description: String): ParameterDescriptor =
    parameterWithName(this)
        .description(description)

fun withMockUser() {
    SecurityContextHolder.getContext().authentication = createJwtAuthentication()
}

fun withMockAdmin() {
    SecurityContextHolder.getContext().authentication =
        createJwtAuthentication(authorities = listOf(SimpleGrantedAuthority(Role.ADMIN.name)))
}

val errorResponseFields = listOf(
    "code" desc "상태 코드",
    "message" desc "에러 메세지"
)