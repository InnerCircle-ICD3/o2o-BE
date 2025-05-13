package com.eatngo.common.error

import com.eatngo.common.error.ErrorCode
import org.slf4j.event.Level
import org.springframework.http.HttpStatus

/**
 * 공통 에러 코드 정의
 * 시스템 전반에서 사용되는 기본적인 에러 코드
 */
enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val logLevel: Level = Level.WARN
) : ErrorCode {
    // Common
    INVALID_INPUT("C001", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED("C002", "지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    ENTITY_NOT_FOUND("C003", "요청한 엔티티를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("C004", "서버 내부 오류가 발생했습니다.",  HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TYPE_VALUE("C005", "잘못된 타입의 값이 입력되었습니다."),
    DUPLICATE_ENTITY("C006", "엔티티가 이미 존재합니다.",HttpStatus.CONFLICT),

    // Security
    UNAUTHORIZED("S001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("S002", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),

    // Validation
    VALIDATION_FAILED("V001", "유효성 검증에 실패했습니다.");
}