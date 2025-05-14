package com.eatngo.common.error

/**
 * 공통 에러 코드 정의
 * 시스템 전반에서 사용되는 기본적인 에러 코드
 */
enum class CommonErrorCode(
    override val code: String,
    override val message: String,
) : ErrorCode {
    // Common
    INVALID_INPUT("C001", "입력값이 올바르지 않습니다."),
    ENTITY_NOT_FOUND("C003", "요청한 엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("C004", "서버 내부 오류가 발생했습니다."),
    INVALID_TYPE_VALUE("C005", "잘못된 타입의 값이 입력되었습니다."),
    DUPLICATE_ENTITY("C006", "엔티티가 이미 존재합니다."),

    // Security
    UNAUTHORIZED("S001", "인증이 필요합니다."),
    FORBIDDEN("S002", "접근이 거부되었습니다."),

    // Validation
    VALIDATION_FAILED("V001", "유효성 검증에 실패했습니다.");
}