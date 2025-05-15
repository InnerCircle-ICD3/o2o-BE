package com.eatngo.customer

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.OrderException
import com.eatngo.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.nio.file.AccessDeniedException

@Tag(name = "테스트", description = "Swagger 테스트용 API")
@RestController
@RequestMapping("/api/v1/test")
class HelloController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "Hello API", description = "사용자용 Swagger 테스트를 위한 Hello API")
    @GetMapping("/hello")
    fun hello(): ApiResponse<String> {
        return ApiResponse.success("Hello, Swagger! 저는 사용자예요!")
    }

    @Operation(summary = "Order Exception 테스트", description = "OrderException 테스트를 위한 API")
    @GetMapping("/error/order")
    fun orderError(): ApiResponse<String> {
        throw OrderException.OrderNotFound("12345")
    }

    @Operation(summary = "Order Already Completed Exception 테스트", description = "주문 완료 예외 테스트를 위한 API")
    @GetMapping("/error/order-completed")
    fun orderCompletedError(): ApiResponse<String> {
        throw OrderException.OrderAlreadyCompleted("12345")
    }

    @Operation(summary = "Runtime Exception 테스트", description = "일반 RuntimeException 테스트를 위한 API")
    @GetMapping("/error/runtime")
    fun runtimeError(): ApiResponse<String> {
        throw RuntimeException("테스트용 런타임 예외 발생")
    }

    @Operation(summary = "Access Denied Exception 테스트", description = "Access Denied Exception 테스트를 위한 API")
    @GetMapping("/error/access-denied")
    fun accessDeniedError(): ApiResponse<String> {
        throw AccessDeniedException("접근 권한이 없습니다")
    }

    data class TestRequestDto(
        @field:NotBlank(message = "이름은 필수입니다")
        @field:Size(min = 2, max = 10, message = "이름은 2~10자 사이여야 합니다")
        val name: String,

        @field:Min(value = 1, message = "나이는 1 이상이어야 합니다")
        val age: Int
    )

    @Operation(summary = "Validation Exception 테스트", description = "입력값 유효성 검증 예외 테스트를 위한 API")
    @PostMapping("/validation")
    fun validationTest(@Valid @RequestBody request: TestRequestDto): ApiResponse<String> {
        return ApiResponse.success("유효성 검증 통과: ${request.name}, ${request.age}세")
    }
}