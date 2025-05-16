package com.eatngo.order

import com.eatngo.order.dto.CreateOrderItemRequestDto
import com.eatngo.order.dto.CreateOrderRequestDto
import com.eatngo.order.dto.OrderDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import io.kotest.extensions.spring.SpringExtension

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest : StringSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    init {
        "POST orders 주문 생성 테스트" {
            val requestDto = CreateOrderRequestDto(
                storeId = 1,
                orderItems = listOf(
                    CreateOrderItemRequestDto(
                        productId = 10L,
                        productName = "테스트상품",
                        price = 2000,
                        quantity = 2
                    )
                )
            )


            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
            val entity = HttpEntity(requestDto, headers)

            val response = restTemplate.postForEntity(
                "/api/v1/orders",
                entity,
                OrderDto::class.java
            )
            response.statusCode shouldBe HttpStatus.OK
        }
    }
}
