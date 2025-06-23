package com.eatngo.handler

import com.eatngo.common.error.CommonErrorCode
import com.eatngo.common.exception.BusinessException
import com.eatngo.common.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
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

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        e: BusinessException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val context = request.toLogContext(e.data)
        e.logError(log, e.logLevel, e.message, context)
        val status = e.errorCode.httpStatus ?: 400
        return ResponseEntity
            .status(status)
            .body(ApiResponse.error(e.errorCode.code, e.message, e.data))
    }

    /** Spring의 @Valid, @Validated 사용 시 처리용 */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val fieldErrors =
            e.bindingResult.fieldErrors.associate { error ->
                error.field to (error.defaultMessage ?: "유효하지 않은 값")
            }
        val context = request.toLogContext().plus("fields" to fieldErrors)
        val firstErrorMessage = fieldErrors.values.firstOrNull() ?: "유효하지 않은 요청"
        e.logError(log, Level.WARN, "유효성 검증 실패: $fieldErrors", context)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT.code, firstErrorMessage))
    }

    /** 예상하지 못한 런타임 오류 처리용*/
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        e: RuntimeException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val context = request.toLogContext()
        e.logError(log, Level.ERROR, "예상치 못한 오류 발생: ${e.message}", context)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse.error(
                    CommonErrorCode.INTERNAL_SERVER_ERROR.code,
                    e.message ?: "서버 내부 오류가 발생했습니다",
                ),
            )
    }

    // --------------------- Helper Methods ---------------------

    /**
     * HttpServletRequest에서 주요 요청 정보를 추출해 로그 컨텍스트 맵으로 변환합니다.
     * @param data 추가로 합치고 싶은 데이터(선택)
     * @return 로그용 컨텍스트 맵 (path, method, userAgent, timestamp 등 포함)
     */
    fun HttpServletRequest.toLogContext(data: Map<String, Any>? = null): Map<String, Any> =
        mutableMapOf<String, Any>(
            "path" to this.requestURI,
            "method" to this.method,
            "userAgent" to (this.getHeader("User-Agent") ?: "Unknown"),
            "timestamp" to LocalDateTime.now().toString(),
        ).apply {
            data?.let { putAll(it) }
        }

    /**
     * Exception을 로거와 함께 지정한 레벨/메시지/컨텍스트로 로깅합니다.
     * @param logger 사용할 Logger 인스턴스
     * @param level 로그 레벨
     * @param message 로그 메시지(선택)
     * @param context 로그 컨텍스트(선택)
     */
    fun Exception.logError(
        logger: Logger,
        level: Level,
        message: String? = null,
        context: Map<String, Any> = emptyMap(),
    ) {
        val logMsg = "[$level] $message | Context: $context"
        when (level) {
            Level.ERROR -> logger.error(logMsg, this)
            Level.WARN -> logger.warn(logMsg)
            else -> logger.info(logMsg)
        }
    }
}
