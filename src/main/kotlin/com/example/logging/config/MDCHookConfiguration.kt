package com.example.logging.config

import jakarta.annotation.PostConstruct
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Hooks
import reactor.core.publisher.Operators
import reactor.core.scheduler.Schedulers

//@Configuration
class MDCHookConfiguration {

    companion object {
        const val MDCHookConfigurationKey = "MDCHookConfiguration"
        const val MDCSchedulerConfigurationKey = "MDCSchedulerConfiguration"
    }

    @PostConstruct
    fun init() {

        //스케쥴러를 통해 실행되는 모든 쓰레드에 MDC를 전파
        Schedulers.onScheduleHook(MDCSchedulerConfigurationKey) {
            val contextMap = MDC.getCopyOfContextMap()
            Runnable {
                contextMap?.also { MDC.setContextMap(it) }
                try {
                    it.run()
                } finally {
                    MDC.clear()
                }
            }
        }

        //reactor의 모든 publisher에 MDC 전파
        Hooks.onEachOperator(MDCHookConfigurationKey, Operators.lift { _, subscriber ->
            val currentMDC = MDC.getCopyOfContextMap()
            MDCPropagationSubscriber(subscriber, currentMDC)
        })
    }
}