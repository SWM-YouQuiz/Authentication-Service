package com.quizit.authentication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthenticationApplication

fun main(args: Array<String>) {
    runApplication<com.quizit.authentication.AuthenticationApplication>(*args)
}