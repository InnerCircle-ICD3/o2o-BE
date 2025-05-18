package com.eatngo.store_owner

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "테스트", description = "Swagger 테스트용 API")
@RestController
@RequestMapping("/api/v1/test")
class HelloController {

    @Operation(summary = "Hello API", description = "점주용 Swagger 테스트를 위한 Hello API")
    @GetMapping("/hello")
    fun hello(): String {
        return "Hello, Swagger! 저는 점주예요!"
    }
}