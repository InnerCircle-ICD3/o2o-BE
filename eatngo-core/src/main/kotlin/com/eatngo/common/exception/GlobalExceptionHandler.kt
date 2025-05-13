package com.eatngo.common.exception

import com.eatngo.common.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.concurrent.ConcurrentHashMap
import org.springframework.web.HttpRequestMethodNotSupportedException
import jakarta.servlet.http.HttpServletRequest

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)
    
    // 에러 카운트 저장 맵 - 모니터링 알람 용도
    private val errorCountMap = ConcurrentHashMap<String, Int>()
    
    // 임계값 설정 - 실제 상황에 맞게 조정 필요
    private val ERROR_THRESHOLD = 10 
    private val WARN_THRESHOLD = 100

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException, request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        // 로깅을 위한 컨텍스트 정보 구성
        val logContext = mapOf(
            "path" to request.requestURI,
            "method" to request.method,
            "errorData" to (e.data ?: emptyMap<String, Any>())
        )
        
        // 로깅 및 에러 카운트 증가
        logException(e, logContext)
        incrementErrorCount(e.errorCode.code)
        
        return ResponseEntity
            .status(determineHttpStatus(e.errorCode))
            .body(ApiResponse.error(e.errorCode.code, e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        val message = e.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        
        // 로깅을 위한 컨텍스트 정보 구성
        val fieldErrors = e.bindingResult.fieldErrors.associate { 
            it.field to (it.rejectedValue?.toString() ?: "null") 
        }
        
        val logContext = mapOf(
            "path" to request.requestURI,
            "method" to request.method,
            "fields" to fieldErrors
        )
        
        log.warn("Validation failed: {}, context: {}", message, logContext)
        incrementErrorCount(ErrorCode.INVALID_INPUT_VALUE.code)
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE.code, message))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException, request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        val errorMessage = "${e.name}의 값(${e.value})이 ${e.requiredType?.simpleName ?: "요구되는 타입"}으로 변환될 수 없습니다."
        
        // 로깅을 위한 컨텍스트 정보 구성
        val logContext = mapOf(
            "path" to request.requestURI,
            "method" to request.method,
            "paramName" to e.name,
            "paramValue" to (e.value?.toString() ?: "null"),
            "requiredType" to (e.requiredType?.simpleName ?: "unknown")
        )
        
        log.warn("Type mismatch: {}, context: {}", errorMessage, logContext)
        incrementErrorCount(ErrorCode.INVALID_TYPE_VALUE.code)
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ErrorCode.INVALID_TYPE_VALUE.code, errorMessage))
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        val errorMessage = "${request.method} 메서드는 ${request.requestURI} 경로에서 지원되지 않습니다. 지원되는 메서드: ${e.supportedMethods?.joinToString(", ") ?: "없음"}"
        
        // 로깅을 위한 컨텍스트 정보 구성
        val logContext = mapOf(
            "path" to request.requestURI,
            "method" to request.method,
            "supportedMethods" to (e.supportedMethods?.toList() ?: emptyList<String>())
        )
        
        log.warn("Method not allowed: {}, context: {}", errorMessage, logContext)
        incrementErrorCount(ErrorCode.METHOD_NOT_ALLOWED.code)
        
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED.code, errorMessage))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        // 로깅을 위한 컨텍스트 정보 구성
        val logContext = mapOf(
            "path" to request.requestURI,
            "method" to request.method,
            "errorType" to e.javaClass.simpleName
        )
        
        // 시스템 레벨의 예외는 항상 스택 트레이스와 함께 로깅
        log.error("Unexpected error occurred: {}, context: {}", e.message, logContext, e)
        incrementErrorCount(ErrorCode.INTERNAL_SERVER_ERROR.code)
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.code, ErrorCode.INTERNAL_SERVER_ERROR.message))
    }

    private fun logException(e: BusinessException, context: Map<String, Any>) {
        when (e.errorCode.logLevel) {
            org.slf4j.event.Level.ERROR -> log.error("Business error occurred: {}, context: {}", e.message, context, e)
            org.slf4j.event.Level.WARN -> log.warn("Business warning: {}, context: {}", e.message, context)
            org.slf4j.event.Level.INFO -> log.info("Business info: {}, context: {}", e.message, context)
            else -> log.debug("Business debug: {}, context: {}", e.message, context)
        }
    }
    
    private fun incrementErrorCount(errorCode: String) {
        val currentCount = errorCountMap.compute(errorCode) { _, count -> (count ?: 0) + 1 }
        
        // 특정 임계값을 초과하면 모니터링 알람 트리거 로직 추가 가능
        if (currentCount != null) {
            val threshold = if (errorCode.startsWith("C")) WARN_THRESHOLD else ERROR_THRESHOLD
            if (currentCount % threshold == 0) {
                log.error("Error threshold exceeded for code {}: {} occurrences", errorCode, currentCount)
                // 여기에 알람 전송 로직 추가 가능
            }
        }
    }
    
    private fun determineHttpStatus(errorCode: ErrorCode): HttpStatus {
        // 에러 코드에 따라 적절한 HTTP 상태 코드 반환
        return when(errorCode) {
            ErrorCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
            ErrorCode.FORBIDDEN -> HttpStatus.FORBIDDEN
            ErrorCode.ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorCode.METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED
            ErrorCode.DUPLICATE_ENTITY -> HttpStatus.CONFLICT
            else -> HttpStatus.BAD_REQUEST
        }
    }
} 