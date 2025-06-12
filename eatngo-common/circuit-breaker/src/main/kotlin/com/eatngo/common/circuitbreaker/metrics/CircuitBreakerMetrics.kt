package com.eatngo.common.circuitbreaker.metrics

import com.eatngo.common.circuitbreaker.service.CircuitBreakerManager
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CircuitBreakerMetrics(
    private val meterRegistry: MeterRegistry?,
    private val circuitBreakerManager: CircuitBreakerManager
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }
    
    private val successCounters = mutableMapOf<String, Counter>()
    private val failureCounters = mutableMapOf<String, Counter>()
    private val fallbackCounters = mutableMapOf<String, Counter>()
    private val timers = mutableMapOf<String, Timer>()
    
    fun recordSuccess(circuitBreakerName: String, executionTime: Duration) {
        meterRegistry?.let {
            getSuccessCounter(circuitBreakerName).increment()
            getTimer(circuitBreakerName).record(executionTime)
        }
        
        logger.debug { "Circuit Breaker [$circuitBreakerName] Success - ${executionTime.toMillis()}ms" }
    }
    
    fun recordFailure(circuitBreakerName: String, executionTime: Duration, exception: Throwable) {
        meterRegistry?.let {
            getFailureCounter(circuitBreakerName).increment()
            getTimer(circuitBreakerName).record(executionTime)
        }
        
        logger.warn { "Circuit Breaker [$circuitBreakerName] Failure - ${executionTime.toMillis()}ms, error: ${exception.message}" }
    }
    
    fun recordFallback(circuitBreakerName: String, reason: String) {
        meterRegistry?.let {
            getFallbackCounter(circuitBreakerName).increment()
        }
        
        logger.info { "Circuit Breaker [$circuitBreakerName] Fallback executed - reason: $reason" }
    }
    
    fun recordStateChange(circuitBreakerName: String, fromState: CircuitBreakerManager.State, toState: CircuitBreakerManager.State) {
        meterRegistry?.let { registry ->
            Counter.builder("circuit_breaker_state_transitions")
                .tag("name", circuitBreakerName)
                .tag("from_state", fromState.name)
                .tag("to_state", toState.name)
                .register(registry)
                .increment()
        }
        
        logger.warn { "Circuit Breaker [$circuitBreakerName] State Changed: $fromState -> $toState" }
    }
    
    private fun getSuccessCounter(name: String): Counter {
        return successCounters.computeIfAbsent(name) {
            Counter.builder("circuit_breaker_calls")
                .tag("name", name)
                .tag("result", "success")
                .description("Circuit breaker successful calls")
                .register(meterRegistry!!)
        }
    }
    
    private fun getFailureCounter(name: String): Counter {
        return failureCounters.computeIfAbsent(name) {
            Counter.builder("circuit_breaker_calls")
                .tag("name", name)
                .tag("result", "failure")
                .description("Circuit breaker failed calls")
                .register(meterRegistry!!)
        }
    }
    
    private fun getFallbackCounter(name: String): Counter {
        return fallbackCounters.computeIfAbsent(name) {
            Counter.builder("circuit_breaker_fallbacks")
                .tag("name", name)
                .description("Circuit breaker fallback executions")
                .register(meterRegistry!!)
        }
    }
    
    private fun getTimer(name: String): Timer {
        return timers.computeIfAbsent(name) {
            Timer.builder("circuit_breaker_calls_duration")
                .tag("name", name)
                .description("Circuit breaker call duration")
                .register(meterRegistry!!)
        }
    }
    
    /**
     * Circuit Breaker 상태를 Gauge로 등록
     */
    fun registerStateGauges() {
        meterRegistry?.let { registry ->
            circuitBreakerManager.getAllStates().forEach { (name, _) ->
                registry.gauge("circuit_breaker_state",
                    listOf(io.micrometer.core.instrument.Tag.of("name", name)),
                    circuitBreakerManager
                ) { manager ->
                    when (manager.getState(name)) {
                        CircuitBreakerManager.State.CLOSED -> 0.0
                        CircuitBreakerManager.State.OPEN -> 1.0
                        CircuitBreakerManager.State.HALF_OPEN -> 2.0
                        null -> -1.0
                    }
                }
                
                // Circuit Breaker 실패 카운트 Gauge
                registry.gauge("circuit_breaker_failure_count",
                    listOf(io.micrometer.core.instrument.Tag.of("name", name)),
                    circuitBreakerManager
                ) { manager ->
                    manager.getFailureCount(name).toDouble()
                }
            }
        }
    }
} 