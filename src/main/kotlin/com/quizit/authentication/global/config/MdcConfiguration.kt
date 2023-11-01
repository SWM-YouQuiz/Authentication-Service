package com.quizit.authentication.global.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.reactivestreams.Subscription
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.CoreSubscriber
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.core.publisher.Operators
import reactor.util.context.Context
import java.util.*
import java.util.stream.Collectors

@Configuration
class MdcConfiguration {
    private val mdcContextKey = MdcConfiguration::class.java.name

    @PostConstruct
    fun contextOperatorHook() {
        Hooks.onEachOperator(
            mdcContextKey, Operators.lift { _, subscriber -> MdcContextLifter(subscriber) })
    }

    @PreDestroy
    fun cleanupHook() {
        Hooks.resetOnEachOperator(mdcContextKey)
    }
}

class MdcContextLifter<T>(private val coreSubscriber: CoreSubscriber<T>) : CoreSubscriber<T> {
    override fun onSubscribe(subscription: Subscription) {
        coreSubscriber.onSubscribe(subscription)
    }

    override fun onNext(t: T) {
        copyToMdc(coreSubscriber.currentContext())
        coreSubscriber.onNext(t)
    }

    override fun onError(throwable: Throwable) {
        coreSubscriber.onError(throwable)
    }

    override fun onComplete() {
        coreSubscriber.onComplete()
    }

    override fun currentContext(): Context = coreSubscriber.currentContext()

    private fun copyToMdc(context: Context) {
        MDC.setContextMap(
            context.stream()
                .collect(
                    Collectors.toMap(
                        { it.key.toString() },
                        { it.value.toString() })
                )
        )
    }
}

@Component
class MdcFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
        chain.filter(exchange)
            .contextWrite { it.put("traceId", UUID.randomUUID().toString().substring(0..7)) }
}