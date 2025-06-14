package com.eatngo.common.circuitbreaker.annotation

/**
 * Redis Circuit Breaker 어노테이션
 * 
 * @param name Circuit Breaker 이름 (메트릭 태그로 사용)
 * @param fallbackMethod fallback 메서드명 (같은 클래스 내에 있어야 함)
 * @param failureThreshold 실패 임계값 (기본 5회)
 * @param timeoutMinutes OPEN 상태 유지 시간 (기본 2분)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RedisCircuitBreaker(
    val name: String,
    val fallbackMethod: String = "",
    val failureThreshold: Int = 5,
    val timeoutMinutes: Long = 2L
) 