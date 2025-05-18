package com.eatngo.store_owner

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class HelloControllerTest : DescribeSpec({
    val controller = HelloController()

    describe("HelloController") {
        context("/api/v1/test/hello 엔드포인트 호출 시") {
            it("Hello, Swagger! 저는 점주예요! 를 반환한다") {
                val result = controller.hello()
                result shouldBe "Hello, Swagger! 저는 점주예요!"
            }
        }
    }
})