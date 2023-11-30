package com.example.logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks


@SpringBootApplication
class LoggingApplication

fun main(args: Array<String>) {
    runApplication<LoggingApplication>(*args)
}