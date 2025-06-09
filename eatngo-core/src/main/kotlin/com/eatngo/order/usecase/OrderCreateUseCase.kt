package com.eatngo.order.usecase

import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.dto.OrderItemCreateDto
import com.eatngo.order.dto.OrderItemSnapshotDto
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
    val productService: ProductService,
    val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun create(orderDto: OrderCreateDto): OrderDto {
        val idToProduct = productService.findAllProducts(orderDto.storeId)
            .associateBy { it.id!! }
        val orderItemDto = orderDto.orderItems

        val orderItemSnapshotDtos =  toOrderItemSnapshotDto(orderItemDto, idToProduct)
        val order = orderService.createOrder(
            storeId = orderDto.storeId,
            customerId = orderDto.customerId,
            pickupDateTime = orderDto.pickupDateTime,
            orderItemSnapshotDtos = orderItemSnapshotDtos
        )

        OrderEvent.from(order, order.customerId)
            ?.let { eventPublisher.publishEvent(it) }

        return OrderDto.from(order)
    }

    private fun toOrderItemSnapshotDto(
        orderItemDto: List<OrderItemCreateDto>,
        idToProduct: Map<Long, ProductDto>
    ) =
        orderItemDto.map {
            val product = idToProduct.getOrDefault(it.productId, null)

            requireNotNull(product) { "존재하지 않는 상품을 선택하셨습니다." }
            require(product.name == it.productName) { "상품 명이 다릅니다." }
            require(product.price.finalPrice == it.price) { "상품 가격이 다릅니다." }
            require(product.inventory.stock >= it.quantity) { " 재고가 충분하지 않습니다." }

            return@map OrderItemSnapshotDto(
                productId = product.id!!,
                productName = product.name,
                quantity = product.inventory.stock,
                originPrice = product.price.originalPrice,
                finalPrice = product.price.finalPrice,
                imageUrl = product.imageUrl
            )
        }.toList()

}

