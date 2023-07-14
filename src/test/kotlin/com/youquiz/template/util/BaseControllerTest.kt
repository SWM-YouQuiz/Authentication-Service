package com.youquiz.template.util

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureRestDocs
abstract class BaseControllerTest : DescribeSpec() {
    protected lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var restDocumentation: RestDocumentationContextProvider

    override suspend fun beforeSpec(spec: Spec) {
        webClient = WebTestClient.bindToApplicationContext(applicationContext)
            .configureClient()
            .baseUrl("http://localhost:8080")
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
            .mutateWith(SecurityMockServerConfigurers.csrf())
    }
}