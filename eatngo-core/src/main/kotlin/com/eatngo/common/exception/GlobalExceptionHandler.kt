package com.eatngo.common.exception

import com.eatngo.common.error.CommonErrorCode
import com.eatngo.common.error.ErrorCode
import com.eatngo.common.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.concurrent.ConcurrentHashMap

/**
 * 전역 예외 처리기
 * 모든 API 예외를 일관된 형식으로 처리
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val errorCounter = ErrorCounter()

    // --------------------- Business Exceptions ---------------------
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        e: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val context = buildLogContext(request, e.data)
        logError(e, e.errorCode.logLevel, e.message, context)
        errorCounter.increment(e.errorCode.code)
        return createResponse(e.errorCode, e.message ?: e.errorCode.message)
    }

    // --------------------- Validation Exceptions ---------------------
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val fieldErrors = e.bindingResult.fieldErrors.associate { 
            it.field to (it.rejectedValue?.toString() ?: "null") 
        }
        val message = fieldErrors.entries.joinToString { "${it.key}=${it.value}" }
        val context = buildLogContext(request).plus("fields" to fieldErrors)

        logError(e, Level.WARN, "Validation failed: $message", context)
        errorCounter.increment(CommonErrorCode.INVALID_INPUT.code)

        return createResponse(
            CommonErrorCode.INVALID_INPUT,
            "${CommonErrorCode.INVALID_INPUT.message}: $message"
        )
    }

    // --------------------- Type Mismatch ---------------------
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        e: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val message = "${e.name}=${e.value} (required type: ${e.requiredType?.simpleName})"
        val context = buildLogContext(request) + mapOf(
            "param" to (e.name ?: "unknown_param"),
            "value" to (e.value?.toString() ?: "null"),
            "requiredType" to (e.requiredType?.simpleName ?: "unknown_type")
        )

        logError(e, Level.WARN, "Type mismatch: $message", context)
        errorCounter.increment(CommonErrorCode.INVALID_TYPE_VALUE.code)

        return createResponse(CommonErrorCode.INVALID_TYPE_VALUE, message)
    }

    // --------------------- Unhandled Exceptions ---------------------
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        val context = buildLogContext(request).plus("exceptionType" to e.javaClass.simpleName)
        logError(e, Level.ERROR, "Unhandled exception: ${e.message}", context)
        errorCounter.increment(CommonErrorCode.INTERNAL_SERVER_ERROR.code)
        return createResponse(CommonErrorCode.INTERNAL_SERVER_ERROR)
    }

    // --------------------- Helper Methods ---------------------
    private fun buildLogContext(
        request: HttpServletRequest,
        data: Map<String, Any>? = null
    ): Map<String, Any> {
        return mutableMapOf<String, Any>( // 타입 명시적 선언
            "path" to request.requestURI,
            "method" to request.method
        ).apply {
            data?.let { putAll(it) }
        }
    }

    private fun logError(e: Exception, level: Level, message: String?, context: Map<String, Any>) {
        when (level) {
            Level.ERROR -> log.error("[$level] $message | Context: $context", e)
            Level.WARN -> log.warn("[$level] $message | Context: $context")
            else -> log.info("[$level] $message | Context: $context")
        }
    }

    private fun createResponse(
        errorCode: ErrorCode,
        message: String = errorCode.message
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(errorCode.httpStatus)
            .body(ApiResponse.error(errorCode.code, message))
    }

    // --------------------- Error Counter ---------------------
    /** 추후 error count 해서 알림 설정 위해서 추가 */
    private inner class ErrorCounter {
        private val counts = ConcurrentHashMap<String, Int>()
        private val thresholds = mapOf(
            "ERROR" to 10,
            "WARN" to 100
        )

        fun increment(code: String) {
            val count = counts.merge(code, 1) { old, _ -> old + 1 } ?: 1
            checkThreshold(code, count)
        }

        private fun checkThreshold(code: String, count: Int) {
            val threshold = when {
                code.startsWith("C") -> thresholds["WARN"]
                else -> thresholds["ERROR"]
            }
            threshold?.takeIf { count % it == 0 }?.let {
                log.warn("[Threshold] Code: $code, Count: $count")
            }
        }
    }
} 