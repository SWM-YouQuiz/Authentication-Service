package com.quizit.authentication.global.util

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import reactor.util.function.Tuple2

operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 = t1

operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 = t2

fun <K, V> multiValueMapOf(vararg pairs: Pair<K, V>): MultiValueMap<K, V> =
    LinkedMultiValueMap<K, V>()
        .apply {
            pairs.forEach { add(it.first, it.second) }
        }