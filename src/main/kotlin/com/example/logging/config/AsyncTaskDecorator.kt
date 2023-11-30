package com.example.logging.config

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

class AsyncTaskDecorator : TaskDecorator {

    override fun decorate(runnable: Runnable): Runnable {
        val context = MDC.getCopyOfContextMap()

        //1)
        return Runnable {
            context?.also { MDC.setContextMap(it) }
            try {
                runnable.run()
            }finally {
                MDC.clear()
            }
        }
    }
}