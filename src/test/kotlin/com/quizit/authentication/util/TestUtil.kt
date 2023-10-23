package com.quizit.authentication.util

import org.reactivestreams.Publisher
import reactor.test.StepVerifier
import reactor.test.StepVerifier.FirstStep

fun <T> Publisher<T>.getResult(): FirstStep<T> = StepVerifier.create(this)