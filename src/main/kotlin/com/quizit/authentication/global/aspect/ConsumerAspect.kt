package com.quizit.authentication.global.aspect

import com.quizit.authentication.global.util.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.cast

@Aspect
@Component
class ConsumerAspect {
    @Around("@within(com.quizit.authentication.global.annotation.Consumer)")
    fun around(joinPoint: ProceedingJoinPoint) {
        (joinPoint.proceed() as Flux<*>)
            .cast<ConsumerRecord<String, out Any>>()
            .doOnNext {
                it.value()
                    .let { logger.info { "Successfully consumed ${it::class.simpleName}: $it" } }
            }
            .subscribe()
    }
}