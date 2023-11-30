package com.example.logging.config

import org.reactivestreams.Subscription
import org.slf4j.MDC
import reactor.core.CoreSubscriber

class MDCPropagationSubscriber<T>(private val subscriber: CoreSubscriber<T>,
                                  private val mdc: Map<String, String>? = null)
    : CoreSubscriber<T> {

    override fun onSubscribe(s: Subscription) {
        subscriber.onSubscribe(s)
    }

    override fun onError(t: Throwable?) {
        subscriber.onError(t)
    }

    override fun onComplete() {
        subscriber.onComplete()
    }

    override fun onNext(t: T) {
        mdc?.also { mdc -> MDC.setContextMap(mdc) }
        subscriber.onNext(t)
        MDC.clear()
    }

}