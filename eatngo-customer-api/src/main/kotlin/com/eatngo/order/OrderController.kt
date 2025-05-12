package com.eatngo.order

import com.eatngo.order.dto.CreateOrderRequestDto
import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.dto.OrderItemCreateDto
import com.eatngo.order.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(
    private val orderService: OrderService
) {
    @PostMapping("/orders")
    fun createOrder(@RequestBody requestDto: CreateOrderRequestDto): OrderDto {
        return orderService.createOrder(
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
    }
}