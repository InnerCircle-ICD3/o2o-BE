package com.eatngo.order.usecase

import com.eatngo.common.exception.order.OrderException
import com.eatngo.customer.service.CustomerService
import com.eatngo.order.domain.Order
import com.eatngo.order.domain.Status
import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.dto.OrderItemCreateDto
import com.eatngo.order.dto.OrderItemSnapshotDto
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.event.OrderEvent
import com.eatngo.order.service.OrderService
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.service.ProductService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class OrderCreateUseCase(
    val orderService: OrderService,
    val customerService: CustomerService,
    val productService: ProductService,
    val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun create(orderDto: OrderCreateDto): OrderDto {
        val customer = customerService.getCustomerById(orderDto.customerId)

        val existOrders =
            orderService
                .findAllByQueryParam(
                    queryParam =
                        OrderQueryParamDto(
                            customerId = orderDto.customerId,
                            status = Status.CREATED,
                            lastId = null,
                            updatedAt = null,
                            storeId = null,
                        ),
                ).contents

        if (existOrders.isNotEmpty()) {
            throw OrderException.OrderCreatedAlreadyExists(existOrders.map(Order::id))
        }

        val idToProduct =
            productService
                .findAllProducts(orderDto.storeId)
                .associateBy { it.id!! }

        val orderItemSnapshotDtos = toOrderItemSnapshotDto(orderDto.orderItems, idToProduct)

        val order =
            orderService.createOrder(
                storeId = orderDto.storeId,
                customerId = orderDto.customerId,
                pickupDateTime = orderDto.pickupDateTime,
                nickname =
                    customer.account
                        .nickname
                        ?.toString() ?: "",
                orderItemSnapshotDtos = orderItemSnapshotDtos,
            )

        OrderEvent
            .from(order, order.customerId)
            ?.let { eventPublisher.publishEvent(it) }

        return OrderDto.from(order)
    }

    private fun toOrderItemSnapshotDto(
        orderItemDto: List<OrderItemCreateDto>,
        idToProduct: Map<Long, ProductDto>,
    ) = orderItemDto
        .map {
            val product = idToProduct.getOrDefault(it.productId, null)

            requireNotNull(product) { "존재하지 않는 상품을 선택하셨습니다." }
            require(product.name == it.productName) { "상품 명이 다릅니다." }
            require(product.price.finalPrice == it.price) { "상품 가격이 다릅니다." }
            require(product.inventory.stock >= it.quantity) { " 재고가 충분하지 않습니다." }

            return@map OrderItemSnapshotDto(
                productId = product.id!!,
                productName = product.name,
                quantity = it.quantity,
                originPrice = product.price.originalPrice,
                finalPrice = product.price.finalPrice,
                imageUrl = product.imageUrl,
            )
        }.toList()
}
