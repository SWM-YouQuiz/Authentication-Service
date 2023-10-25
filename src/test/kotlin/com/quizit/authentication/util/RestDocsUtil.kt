package com.quizit.authentication.util

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import org.springframework.restdocs.cookies.CookieDescriptor
import org.springframework.restdocs.cookies.CookieDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec

infix fun String.desc(description: String): FieldDescriptor =
    PayloadDocumentation.fieldWithPath(this)
        .description(description)

infix fun String.paramDesc(description: String): ParameterDescriptor =
    RequestDocumentation.parameterWithName(this)
        .description(description)

infix fun String.cookieDesc(description: String): CookieDescriptor =
    CookieDocumentation.cookieWithName(this)
        .description(description)

fun List<FieldDescriptor>.toListFields(): List<FieldDescriptor> =
    this.map { "[].${it.path}" desc it.description as String }

fun <T> BodySpec<T, *>.document(
    identifier: String,
    vararg snippets: Snippet
): BodySpec<T, *> =
    consumeWith(
        WebTestClientRestDocumentationWrapper.document<T>(
            identifier,
            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
            *snippets
        )
    )

fun BodyContentSpec.document(
    identifier: String,
    vararg snippets: Snippet
): BodyContentSpec =
    consumeWith(
        WebTestClientRestDocumentationWrapper.document<ByteArray>(
            identifier,
            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
            *snippets
        )
    )

val errorResponseFields = listOf(
    "code" desc "상태 코드",
    "message" desc "에러 메세지"
)