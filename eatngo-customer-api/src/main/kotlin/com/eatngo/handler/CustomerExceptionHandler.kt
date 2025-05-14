package com.eatngo.handler

import com.eatngo.common.error.CommonErrorCode
import com.eatngo.common.exception.OrderException
import com.eatngo.common.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime


@RestControllerAdvice
class CustomerApiExceptionHandler {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @ExceptionHandler(OrderException::class)
    fun handleOrderException(e: OrderException, request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        // context 정보 구성
        val context = buildLogContext(request, e.data)

        // HTTP 상태 결정
        val (httpStatus, logLevel) = when (e) {
            is OrderException.OrderNotFound -> HttpStatus.NOT_FOUND to Level.WARN
            is OrderException.OrderAlreadyCompleted -> HttpStatus.CONFLICT to Level.WARN
            else -> HttpStatus.BAD_REQUEST to Level.ERROR
        }

        // 로그 메시지 기록
        logError(e, logLevel, e.message, context)

        return ResponseEntity
            .status(httpStatus)
            .body(ApiResponse.error(e.errorCode.code, e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        // 필드 에러 정보 추출
        val fieldErrors = e.bindingResult.fieldErrors.associate { error ->
            error.field to (error.defaultMessage ?: "유효하지 않은 값")
        }

        // 컨텍스트 정보 구성
        val context = buildLogContext(request).plus("fields" to fieldErrors)

        // 첫 번째 에러 메시지 가져오기
        val firstErrorMessage = fieldErrors.values.firstOrNull() ?: "유효하지 않은 요청"

        // 로그 기록
        logError(e, Level.WARN, "유효성 검증 실패: $fieldErrors", context)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT.code, firstErrorMessage))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        e: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        // 컨텍스트 정보 구성
        val context = buildLogContext(request)

        // 로그 기록
        logError(e, Level.WARN, CommonErrorCode.FORBIDDEN.message, context)

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(CommonErrorCode.FORBIDDEN.code, CommonErrorCode.FORBIDDEN.message))
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        e: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        // 컨텍스트 정보 구성
        val context = buildLogContext(request)

        // 로그 기록
        logError(e, Level.ERROR, "예상치 못한 오류 발생: ${e.message}", context)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse.error(
                    CommonErrorCode.INTERNAL_SERVER_ERROR.code,
                    e.message ?: "서버 내부 오류가 발생했습니다"
                )
            )
    }

    // --------------------- Helper Methods ---------------------
    private fun buildLogContext(
        request: HttpServletRequest,
        data: Map<String, Any>? = null
    ): Map<String, Any> {
        return mutableMapOf<String, Any>(
            "path" to request.requestURI,
            "method" to request.method,
            "userAgent" to (request.getHeader("User-Agent") ?: "Unknown"),
            "timestamp" to LocalDateTime.now().toString()
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
}

