package com.eatngo.common.exception

open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
    val cause: Throwable? = null
) : RuntimeException(message, cause) {
    // Stack 생성 비용 최소화를 위해 fillInStackTrace 오버라이딩
    override fun fillInStackTrace(): Throwable {
        return if (errorCode.logLevel.toInt() >= org.slf4j.event.Level.ERROR.toInt()) {
            // ERROR 이상 로그 레벨에서만 스택 트레이스 수집
            super.fillInStackTrace()
        } else {
            this
        }
    }
} 