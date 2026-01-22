package com.eatngo.common.circuitbreaker.exception

/**
 * 외부 시스템 장애로 인한 Circuit Breaker 트리거 예외
 *
 * BusinessException(재고 부족, 상품 없음 등)과 구분하기 위해 사용
 *
 * @property message 예외 메시지
 * @property cause 원인 예외 (optional)
 */
class CircuitBreakerException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
