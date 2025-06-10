package com.eatngo.extension

import com.eatngo.common.exception.OperationRetryException

/**
 * 재시도 로직
 * 
 * @param maxAttempts 최대 재시도 횟수 (기본값: 3)
 * @param delayMs 기본 지연시간 (기본값: 1000ms)
 * @param useExponentialBackoff 지수 백오프 사용 여부 (기본값: true)
 * @param operation 실행할 작업
 * @return 작업 결과
 * @throws OperationRetryException 모든 재시도 실패 시
 */
fun <T> executeWithRetry(
    maxAttempts: Int = 3,
    delayMs: Long = 1000,
    useExponentialBackoff: Boolean = true,
    operation: () -> T
): T {
    var lastException: Exception? = null
    val operationName = operation.javaClass.enclosingMethod?.name ?: "unknown"
    
    repeat(maxAttempts) { attempt ->
        try {
            return operation()
        } catch (e: Exception) {
            lastException = e
            
            if (attempt < maxAttempts - 1) {
                val delay = if (useExponentialBackoff) {
                    delayMs * (1L shl attempt)
                } else {
                    delayMs
                }
                Thread.sleep(delay)
            }
        }
    }
    
    // 모든 재시도 실패 시 OperationRetryException throw
    throw OperationRetryException(
        operationName = operationName,
        originalException = lastException,
        attemptCount = maxAttempts
    )
}