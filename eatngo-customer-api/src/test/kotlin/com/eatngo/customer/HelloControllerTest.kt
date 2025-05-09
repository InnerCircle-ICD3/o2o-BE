package com.eatngo.customer

import com.eatngo.kotest.KotestBase
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

class HelloControllerTest : KotestBase() {
    init {
        "GET /api/v1/test/hello는 Hello, Swagger! 저는 사용자예요! 를 반환한다" {
            val response = restTemplate.getForEntity("/api/v1/test/hello", String::class.java)
            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe "Hello, Swagger! 저는 사용자예요!"
        }
    }
}