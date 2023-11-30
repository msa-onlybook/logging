package com.example.logging.controller

import com.example.logging.service.FeatureService
import kotlinx.coroutines.*
import kotlinx.coroutines.slf4j.MDCContext
import org.apache.commons.logging.LogFactory
import org.slf4j.MDC
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.context.Context
import java.util.concurrent.Executors


@RestController
class ApiController(private val featureService: FeatureService) {

    private val log = LogFactory.getLog(ApiController::class.java)

    @GetMapping("/api/feature")
    fun feature(): ResponseEntity<String> {

        log.info("call feature api")
        val result = featureService.invokeFeatureApi()
        featureService.processAsyncTask()
        return ResponseEntity.ok(result)
    }

    @GetMapping("/api/thread/feature")
    fun threadFeature(): ResponseEntity<String> {

        log.info("call feature api")
        val result = featureService.invokeThreadFeatureApi()
        featureService.processAsyncTask()
        return ResponseEntity.ok(result)
    }

    @GetMapping("/api/reactive/feature")
    fun reactiveFeature(): Mono<String> {
        log.info("call feature api")
        return featureService.invokeReactiveFeatureApi()
                .doOnSuccess {
                    Mono.fromCallable {
                        featureService.processAsyncTask()
                    }.subscribeOn(Schedulers.boundedElastic()).subscribe()
                }
    }

    val Dispatchers.LOOM: CoroutineDispatcher
        get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    @GetMapping("/api/coroutine/feature")
    suspend fun coroutineFeature() = supervisorScope {
        log.info("call feature api")

        withContext(Dispatchers.IO + MDCContext()) {
            featureService.invokeCoroutineFeatureApi().also {
                featureService.processAsyncTask()
            }
        }
    }
}