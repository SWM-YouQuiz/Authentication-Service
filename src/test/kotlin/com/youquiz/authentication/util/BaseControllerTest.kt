package com.youquiz.authentication.util

import com.youquiz.authentication.config.SecurityTestConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@ContextConfiguration(classes = [SecurityTestConfiguration::class])
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
            .baseUrl("/api")
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }
}