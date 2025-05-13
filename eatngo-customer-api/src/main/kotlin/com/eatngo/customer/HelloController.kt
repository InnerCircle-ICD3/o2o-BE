package com.eatngo.customer

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import com.eatngo.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory

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

    @Operation(summary = "Error API", description = "에러 테스트를 위한 API")
    @GetMapping("/error")
    fun error(): ApiResponse<String> {
        throw BusinessException.of(
            errorCode = BusinessErrorCode.ORDER_NOT_FOUND,
            message = "테스트 주문 오류",
            data = mapOf("orderId" to "123")
        )
    }
}