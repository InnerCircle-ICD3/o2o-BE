package com.eatngo.customer

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
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

        context("에러 케이스") {
            it("BusinessException 발생 및 에러 코드 검증") {
                val controller = HelloController()
                val exception = shouldThrow<BusinessException> {
                    controller.error()
                }

                // 에러 코드 검증
                exception.errorCode.code shouldBe BusinessErrorCode.ORDER_NOT_FOUND.code
                exception.message shouldBe "테스트 주문 오류"
                exception.data?.get("orderId") shouldBe "123"
            }
        }
    }
})