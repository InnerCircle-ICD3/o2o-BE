// src/test/kotlin/com/eatngo/order/service/OrderServiceTest.kt
package com.eatngo.order.service

import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.domain.Status
import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.dto.OrderItemCreateDto
import com.eatngo.order.dto.OrderItemDto
import com.eatngo.order.infra.OrderPersistence
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MockOrderPersistenceImpl : OrderPersistence {
    override fun save(order: Order): Order {
        return Order(
            id = 1L,
            storeId = order.storeId,
            orderNumber = 1L,
            orderItems = order.orderItems.mapIndexed { index, orderItem ->
                OrderItem(
                    id = index.toLong(),
                    productId = orderItem.productId,
                    productName = orderItem.productName,
                    quantity = orderItem.quantity,
                    price = orderItem.price,
                )
            },
            customerId = order.customerId,
            status = order.status,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
        )
    }
}

class OrderServiceTest : BehaviorSpec({
    given("유효한 OrderCreateDto가 주어졌을 때") {
        val service = OrderService(MockOrderPersistenceImpl())

        // 테스트용 입력 DTO
        val createDto = OrderCreateDto(
            customerId = 42L,
            storeId = 99L,
            orderItems = listOf(
                OrderItemCreateDto(
                    productId = 10L,
                    productName = "테스트상품",
                    price = 5000,
                    quantity = 3
                )
            )
        )

        `when`("service가 dto를 입력받고") {
            val result: OrderDto = service.createOrder(createDto)

            then("id와 orderNumber를 매핑해서 반환한다") {
                // Order.create 로 생성된 도메인 객체를 기대값으로 사용
                val expectedOrder = OrderDto(
                    id = 1L,
                    orderNumber = 1L,
                    customerId = createDto.customerId,
                    storeId = createDto.storeId,
                    orderItems = createDto.orderItems.mapIndexed { index, orderItemCreateDto ->
                        OrderItemDto(
                            id = index.toLong(),
                            productId = orderItemCreateDto.productId,
                            productName = orderItemCreateDto.productName,
                            price = orderItemCreateDto.price,
                            quantity = orderItemCreateDto.quantity
                        )
                    },
                    status = Status.CREATED.toString(),
                    createdAt = result.createdAt,
                    updatedAt = result.updatedAt,
                )

                result shouldBe expectedOrder
            }
        }
    }
})
