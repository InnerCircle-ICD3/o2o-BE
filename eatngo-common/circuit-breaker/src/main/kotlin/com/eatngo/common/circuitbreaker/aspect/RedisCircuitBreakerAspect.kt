package com.eatngo.common.circuitbreaker.aspect

import com.eatngo.common.circuitbreaker.annotation.RedisCircuitBreaker
import com.eatngo.common.circuitbreaker.exception.BusinessExceptionMarker
import com.eatngo.common.circuitbreaker.metrics.CircuitBreakerMetrics
import com.eatngo.common.circuitbreaker.service.CircuitBreakerManager
import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.time.Duration
import java.time.Instant

@Aspect
@Component
class RedisCircuitBreakerAspect(
    private val circuitBreakerManager: CircuitBreakerManager,
    private val circuitBreakerMetrics: CircuitBreakerMetrics
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }
    
    @Around("@annotation(redisCircuitBreaker)")
    fun aroundRedisCircuitBreaker(joinPoint: ProceedingJoinPoint, redisCircuitBreaker: RedisCircuitBreaker): Any? {
        val circuitBreaker = circuitBreakerManager.getOrCreate(
            redisCircuitBreaker.name,
            redisCircuitBreaker.failureThreshold,
            redisCircuitBreaker.timeoutMinutes,
            redisCircuitBreaker.successThreshold
        )
        
        val startTime = Instant.now()
        val previousState = circuitBreaker.state.get()
        
        return try {
            if (circuitBreakerManager.canExecute(circuitBreaker)) {
                val result = joinPoint.proceed()
                val executionTime = Duration.between(startTime, Instant.now())
                
                // 성공 처리
                circuitBreakerManager.onSuccess(circuitBreaker)
                circuitBreakerMetrics.recordSuccess(redisCircuitBreaker.name, executionTime)
                
                // 상태 변경 체크
                val currentState = circuitBreaker.state.get()
                if (previousState != currentState) {
                    logger.info { "Circuit Breaker [${redisCircuitBreaker.name}] state changed: $previousState -> $currentState" }
                    circuitBreakerMetrics.recordStateChange(redisCircuitBreaker.name, previousState, currentState)
                }
                
                result
            } else {
                // Circuit Breaker가 OPEN 상태인 경우 fallback 실행
                logger.warn { "Circuit Breaker [${redisCircuitBreaker.name}] is OPEN, executing fallback" }
                executeFallback(joinPoint, redisCircuitBreaker, "Circuit breaker is OPEN", null)
            }
        } catch (ex: Exception) {
            val executionTime = Duration.between(startTime, Instant.now())

            // 비즈니스 예외는 Circuit Breaker 대상이 아님
            if (ex is BusinessExceptionMarker) {
                logger.warn { "BusinessException occurred (not counted for circuit breaker): ${ex.message}" }
                throw ex
            }

            // 시스템 장애만 Circuit Breaker에 카운트
            circuitBreakerManager.onFailure(circuitBreaker)
            circuitBreakerMetrics.recordFailure(redisCircuitBreaker.name, executionTime, ex)

            // 상태 변경 체크
            val currentState = circuitBreaker.state.get()
            if (previousState != currentState) {
                logger.warn { "Circuit Breaker [${redisCircuitBreaker.name}] state changed due to failure: $previousState -> $currentState" }
                circuitBreakerMetrics.recordStateChange(redisCircuitBreaker.name, previousState, currentState)
            }

            // 실패 로그
            logger.error { "Circuit Breaker [${redisCircuitBreaker.name}] execution failed: ${ex.message}" }

            // fallback 실행 (실제 예외 전달)
            executeFallback(joinPoint, redisCircuitBreaker, "Exception: ${ex.message}", ex)
        }
    }
    
    private fun executeFallback(
        joinPoint: ProceedingJoinPoint,
        redisCircuitBreaker: RedisCircuitBreaker,
        reason: String,
        ex: Exception? = null
    ): Any? {
        val fallbackMethodName = redisCircuitBreaker.fallbackMethod

        return if (fallbackMethodName.isNotEmpty()) {
            try {
                logger.info { "Circuit Breaker [${redisCircuitBreaker.name}] executing fallback: $fallbackMethodName" }
                circuitBreakerMetrics.recordFallback(redisCircuitBreaker.name, reason)
                invokeFallbackMethod(joinPoint, fallbackMethodName, ex)
            } catch (fallbackEx: Exception) {
                logger.error(fallbackEx) { "Circuit Breaker [${redisCircuitBreaker.name}] fallback failed: $fallbackMethodName" }
                throw fallbackEx
            }
        } else {
            logger.warn { "Circuit Breaker [${redisCircuitBreaker.name}] no fallback method specified" }
            null
        }
    }
    
    private fun invokeFallbackMethod(
        joinPoint: ProceedingJoinPoint,
        fallbackMethodName: String,
        ex: Exception? = null
    ): Any? {
        val target = joinPoint.target
        val targetClass = target.javaClass
        val originalArgs = joinPoint.args

        // fallback 메서드 찾기 (파라미터 타입 + Exception 타입)
        val fallbackMethod = findFallbackMethod(targetClass, fallbackMethodName, originalArgs)
            ?: throw NoSuchMethodException("Fallback method '$fallbackMethodName' not found in ${targetClass.simpleName}")

        return if (fallbackMethod.parameterCount == originalArgs.size + 1) {
            // Exception 파라미터가 있는 경우 - 실제 예외 전달
            val argsWithException = originalArgs + (ex ?: RuntimeException("Circuit breaker triggered"))
            fallbackMethod.invoke(target, *argsWithException)
        } else {
            // Exception 파라미터가 없는 경우
            fallbackMethod.invoke(target, *originalArgs)
        }
    }
    
    private fun findFallbackMethod(targetClass: Class<*>, methodName: String, args: Array<Any?>): Method? {
        val parameterTypes = args.map { it?.javaClass ?: Any::class.java }.toTypedArray()
        
        return try {
            // 정확한 파라미터 타입으로 찾기
            targetClass.getDeclaredMethod(methodName, *parameterTypes)
        } catch (ex: NoSuchMethodException) {
            try {
                // Exception 파라미터 추가해서 찾기
                targetClass.getDeclaredMethod(methodName, *parameterTypes, Exception::class.java)
            } catch (ex2: NoSuchMethodException) {
                // 메서드명만으로 찾기 (파라미터 개수가 맞는 첫 번째 메서드)
                targetClass.declaredMethods.find { method ->
                    method.name == methodName && 
                    (method.parameterCount == args.size || method.parameterCount == args.size + 1)
                }
            }
        }
    }
} 