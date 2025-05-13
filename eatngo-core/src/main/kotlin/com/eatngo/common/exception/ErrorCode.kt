package com.eatngo.common.exception

import org.slf4j.event.Level

enum class ErrorCode(
    val code: String,
    val message: String,
    val logLevel: Level = Level.ERROR
) {
    // Common
    INVALID_INPUT_VALUE("C001", "입력값이 올바르지 않습니다.", Level.WARN),
    METHOD_NOT_ALLOWED("C002", "지원하지 않는 HTTP 메서드입니다.", Level.WARN),
    ENTITY_NOT_FOUND("C003", "요청한 엔티티를 찾을 수 없습니다.", Level.WARN),
    INTERNAL_SERVER_ERROR("C004", "서버 내부 오류가 발생했습니다.", Level.ERROR),
    INVALID_TYPE_VALUE("C005", "잘못된 타입의 값이 입력되었습니다.", Level.WARN),
    DUPLICATE_ENTITY("C006", "엔티티가 이미 존재합니다.", Level.WARN),
    
    // Business
    BUSINESS_EXCEPTION("B001", "비즈니스 로직 처리 중 오류가 발생했습니다.", Level.ERROR),
    
    // Security
    UNAUTHORIZED("S001", "인증이 필요합니다.", Level.WARN),
    FORBIDDEN("S002", "접근이 거부되었습니다.", Level.WARN),
    
    // Validation
    VALIDATION_FAILED("V001", "유효성 검증에 실패했습니다.", Level.WARN);
} 