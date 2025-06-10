package com.eatngo.common.exception

import com.eatngo.common.error.CommonErrorCode
import org.slf4j.event.Level

/**
 * 작업 재시도 실패 예외
 */
class OperationRetryException(
    val operationName: String,
    val errorCode: CommonErrorCode = CommonErrorCode.OPERATION_RETRY_FAILED,
    override val message: String = "${CommonErrorCode.OPERATION_RETRY_FAILED.message}: $operationName (재시도 실패)",
    val attemptCount: Int,
    val originalException: Throwable? = null
) : RuntimeException(message, originalException) {

    val data: Map<String, Any?> = mapOf(
        "operationName" to operationName,
        "attemptCount" to attemptCount,
        "originalError" to (originalException?.message ?: "알 수 없는 오류")
    )

    val logLevel: Level = Level.ERROR
}