package com.eatngo.common.error

/**
 * 공통 에러 코드 정의
 * 시스템 전반에서 사용되는 기본적인 에러 코드
 */
enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val httpStatus: Int? = null
) : ErrorCode {
    // Common
    INVALID_INPUT("CEC001", "입력값이 올바르지 않습니다.", 400),
    ENTITY_NOT_FOUND("CEC003", "요청한 엔티티를 찾을 수 없습니다.", 404),
    INTERNAL_SERVER_ERROR("CEC004", "서버 내부 오류가 발생했습니다.", 500),
    INVALID_TYPE_VALUE("CEC005", "잘못된 타입의 값이 입력되었습니다.", 400),
    DUPLICATE_ENTITY("CEC006", "엔티티가 이미 존재합니다.", 409),

    // Validation
    VALIDATION_FAILED("CEC007", "유효성 검증에 실패했습니다.", 400),

    // Image
    IMAGE_URL_RESOLVE_FAILED("CEC008","이미지 URL 변환에 실패했습니다.", 500),

    // Retry
    OPERATION_RETRY_FAILED("CEC009", "작업 재시도가 실패했습니다.", 500);
}