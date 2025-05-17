package com.eatngo.customer

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.response.ApiResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.slf4j.LoggerFactory

class HelloControllerTest : DescribeSpec({
    val log = LoggerFactory.getLogger(this::class.java)

    describe("HelloController") {
        context("정상 요청") {
            it("공통 성공 응답 반환") {
                val controller = HelloController()
                val result = controller.hello()
                result shouldBe ApiResponse.success("Hello, Swagger! 저는 사용자예요!")
            }
        }
    }
})