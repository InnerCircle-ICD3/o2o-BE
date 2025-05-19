package com.eatngo.order

import com.eatngo.order.domain.Status
import com.eatngo.order.dto.*
import com.eatngo.order.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@Tag(name = "주문", description = "주문 관련 API")
@RestController
class OrderController(
    private val orderService: OrderService
) {
    @PostMapping("/api/v1/orders")
    @Operation(summary = "주문 생성", description = "주문 생성")
    fun createOrder(@RequestBody requestDto: CreateOrderRequestDto): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(
            orderService.createOrder(
                OrderCreateDto(
                    customerId = 1L,
                    storeId = requestDto.storeId,
                    orderItems = requestDto.orderItems.map {
                        OrderItemCreateDto(
                            productId = it.productId,
                            productName = it.productName,
                            quantity = it.quantity,
                            price = it.price,
                        )
                    }
                )
            )
        )
    }

    @PostMapping("/api/v1/orders/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "주문 취소")
    fun cancelOrder(@PathVariable orderId: Long) = ResponseEntity.ok(Unit)

    @PostMapping("/api/v1/orders/{orderId}/done")
    @Operation(summary = "주문 완료", description = "주문 완료")
    fun doneOrder(@PathVariable orderId: Long) = ResponseEntity.ok(Unit)

    @GetMapping("/api/v1/customers/{customerId}/orders")
    @Operation(summary = "내 주문 조회", description = "내 주문 이력 조회")
    fun getOrdersByCustomerId(@PathVariable("customerId") customerId: Long): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(
            listOf(
                OrderDto(
                    id = 1L,
                    orderNumber = 1L,
                    customerId = 1L,
                    storeId = 1L,
                    status = Status.DONE.toString(),
                    orderItems = listOf(
                        OrderItemDto(
                            id = 1L,
                            productId = 1L,
                            productName = "Product 1",
                            quantity = 1,
                            price = 1000
                        )
                    ),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                ),
                OrderDto(
                    id = 2L,
                    orderNumber = 2L,
                    customerId = 2L,
                    storeId = 2L,
                    status = Status.CANCELED.toString(),
                    orderItems = listOf(
                        OrderItemDto(
                            id = 2L,
                            productId = 2L,
                            productName = "Product 1",
                            quantity = 1,
                            price = 1000
                        )
                    ),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                ),
                OrderDto(
                    id = 3L,
                    orderNumber = 3L,
                    customerId = 3L,
                    storeId = 3L,
                    status = Status.CREATED.toString(),
                    orderItems = listOf(
                        OrderItemDto(
                            id = 3L,
                            productId = 3L,
                            productName = "Product 1",
                            quantity = 1,
                            price = 1000
                        )
                    ),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                ),
                OrderDto(
                    id = 4L,
                    orderNumber = 4L,
                    customerId = 4L,
                    storeId = 4L,
                    status = Status.CONFIRMED.toString(),
                    orderItems = listOf(
                        OrderItemDto(
                            id = 4L,
                            productId = 4L,
                            productName = "Product 1",
                            quantity = 1,
                            price = 1000
                        )
                    ),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )
        )
    }
}