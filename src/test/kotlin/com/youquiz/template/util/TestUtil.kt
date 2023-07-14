package com.youquiz.template.util

import io.mockk.every
import io.mockk.mockk
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

infix fun String.desc(description: String): FieldDescriptor =
    fieldWithPath(this).description(description)

infix fun String.paramDesc(description: String): ParameterDescriptor =
    parameterWithName(this).description(description)

fun withMockUser() {
    SecurityContextHolder.getContext().authentication = mockk<Authentication>().also {
        every { it.isAuthenticated } returns true
    }
}

val errorResponseFields = listOf(
    "code" desc "상태 코드",
    "message" desc "에러 메세지"
)