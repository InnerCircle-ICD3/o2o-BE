package com.eatngo.common.circuitbreaker.exception

/**
 * Circuit Breaker에서 제외할 비즈니스 예외를 식별하기 위한 마커 인터페이스
 *
 * 비즈니스 예외(재고 부족, 상품 없음 등)는 시스템 장애가 아니므로
 * Circuit Breaker에 카운트하지 않습니다.
 */
interface BusinessExceptionMarker
