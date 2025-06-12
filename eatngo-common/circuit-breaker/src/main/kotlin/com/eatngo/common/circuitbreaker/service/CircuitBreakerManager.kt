package com.eatngo.common.circuitbreaker.service

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@Component
class CircuitBreakerManager {
    
    private val circuitBreakers = ConcurrentHashMap<String, CircuitBreakerState>()
    
    enum class State {
        CLOSED,    // 정상 상태
        OPEN,      // 장애 상태
        HALF_OPEN  // 복구 시도 상태
    }
    
    data class CircuitBreakerState(
        val name: String,
        val failureThreshold: Int,
        val timeoutMinutes: Long,
        val failureCount: AtomicInteger = AtomicInteger(0),
        val lastFailureTime: AtomicReference<LocalDateTime> = AtomicReference(),
        val state: AtomicReference<State> = AtomicReference(State.CLOSED)
    )
    
    fun getOrCreate(name: String, failureThreshold: Int, timeoutMinutes: Long): CircuitBreakerState {
        return circuitBreakers.computeIfAbsent(name) {
            CircuitBreakerState(name, failureThreshold, timeoutMinutes)
        }
    }
    
    fun canExecute(circuitBreaker: CircuitBreakerState): Boolean {
        return when (circuitBreaker.state.get()) {
            State.CLOSED -> true
            State.HALF_OPEN -> true
            State.OPEN -> shouldAttemptReset(circuitBreaker)
        }
    }
    
    fun onSuccess(circuitBreaker: CircuitBreakerState) {
        circuitBreaker.failureCount.set(0)
        circuitBreaker.state.set(State.CLOSED)
    }
    
    fun onFailure(circuitBreaker: CircuitBreakerState) {
        circuitBreaker.lastFailureTime.set(LocalDateTime.now())
        val failures = circuitBreaker.failureCount.incrementAndGet()
        
        if (failures >= circuitBreaker.failureThreshold) {
            circuitBreaker.state.set(State.OPEN)
        }
    }
    
    private fun shouldAttemptReset(circuitBreaker: CircuitBreakerState): Boolean {
        val lastFailure = circuitBreaker.lastFailureTime.get() ?: return false
        val shouldReset = lastFailure.plusMinutes(circuitBreaker.timeoutMinutes).isBefore(LocalDateTime.now())
        
        if (shouldReset) {
            circuitBreaker.state.set(State.HALF_OPEN)
        }
        
        return shouldReset
    }
    
    fun getAllStates(): Map<String, State> {
        return circuitBreakers.mapValues { it.value.state.get() }
    }
    
    fun getState(name: String): State? {
        return circuitBreakers[name]?.state?.get()
    }
    
    fun getFailureCount(name: String): Int {
        return circuitBreakers[name]?.failureCount?.get() ?: 0
    }
} 