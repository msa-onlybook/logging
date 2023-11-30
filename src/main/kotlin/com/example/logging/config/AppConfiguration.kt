package com.example.logging.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.Executors


@EnableAsync
@Configuration
class AppConfiguration {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }

    @Bean
    fun webClient(builder:WebClient.Builder):WebClient{
        return builder.build()
    }

//    @Bean
//    fun virtualThreadTaskExecutor(): TaskExecutor {
//        return TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor()).apply {
//            setTaskDecorator(AsyncTaskDecorator())
//        }
//    }

    @Bean(name = ["taskExecutor"])
    fun taskExecutor(): TaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 5
            maxPoolSize = 10
            queueCapacity = 25
            setThreadNamePrefix("async-pool")
            setTaskDecorator(AsyncTaskDecorator())
            initialize()
        }
    }
}


