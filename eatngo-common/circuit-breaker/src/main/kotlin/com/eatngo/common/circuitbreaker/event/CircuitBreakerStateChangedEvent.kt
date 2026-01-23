package com.eatngo.common.circuitbreaker.event

import com.eatngo.common.circuitbreaker.service.CircuitBreakerManager
import org.springframework.context.ApplicationEvent

data class CircuitBreakerStateChangedEvent(
    val circuitBreakerName: String,
    val previousState: CircuitBreakerManager.State,
    val currentState: CircuitBreakerManager.State,
    val timestamp: java.time.Instant = java.time.Instant.now()
) : ApplicationEvent(circuitBreakerName)
