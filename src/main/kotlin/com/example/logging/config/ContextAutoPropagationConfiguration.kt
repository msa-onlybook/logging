package com.example.logging.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Hooks

@Configuration
class ContextAutoPropagationConfiguration {

    @PostConstruct
    fun init() {
        Hooks.enableAutomaticContextPropagation()
    }
}