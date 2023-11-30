package com.example.logging.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.apache.commons.logging.LogFactory
import org.slf4j.MDC
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Service
class FeatureService(private val restTemplate: RestTemplate,
                     private val webClient: WebClient) {

    companion object {
        private const val featureApiUrl = "http://localhost:8081/api/feature"
    }

    private val log = LogFactory.getLog(FeatureService::class.java)

    val Dispatchers.LOOM: CoroutineDispatcher
        get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()


    fun invokeFeatureApi(): String {

        // feature api 호출
        log.info("request feature api $featureApiUrl")
        val result = restTemplate.getForEntity(featureApiUrl, String::class.java)

        // api 호출 결과
        val response = result.body ?: "error"
        log.info("receive feature api response = $response")

        // return result
        return response
    }

    fun invokeThreadFeatureApi(): String {

        // feature api 호출
        log.info("request feature api $featureApiUrl")
        val result = restTemplate.getForEntity(featureApiUrl, String::class.java)

        // api 호출 결과
        val response = result.body ?: "error"
        log.info("receive feature api response = $response")

        val currentMDC = MDC.getCopyOfContextMap()
        CompletableFuture.runAsync {
            currentMDC?.also { MDC.setContextMap(it) }
            processAsyncTask()
            MDC.clear()
        }


        // return result
        return response
    }


    fun invokeReactiveFeatureApi(): Mono<String> {
        log.info("request feature api $featureApiUrl")
//        val currentMDC = MDC.getCopyOfContextMap()
        return webClient
                .get()
                .uri(featureApiUrl)
                .retrieve()
                .bodyToMono(String::class.java)
                .doOnNext {
//                    currentMDC?.also { mdc -> MDC.setContextMap(mdc) }
                    log.info("receive feature api response = $it")
                }.onErrorReturn("error")
    }

    suspend fun invokeCoroutineFeatureApi(): String = withContext(Dispatchers.IO + MDCContext()) {
        log.info("request feature api $featureApiUrl")

        val response = webClient.get()
                .uri(featureApiUrl)
                .retrieve()
                .awaitBodyOrNull() ?: "error"

        response.also {
            log.info("receive feature api response = $it")
            processAsyncTaskByVirtualThread()
        }

    }

    @Async
    fun processAsyncTask() {
        log.info("doing some async task")
        processAsyncTaskByVirtualThread()
    }

    fun processAsyncTaskByVirtualThread(){
        val currentMDC = MDC.getCopyOfContextMap()
        Thread.ofVirtual().start{
            currentMDC?.also { MDC.setContextMap(it) }
            log.info("doing some virtual thread task")
        }
    }
}

