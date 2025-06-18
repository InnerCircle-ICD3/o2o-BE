package com.eatngo.order.service

import com.eatngo.common.exception.order.OrderException
import com.eatngo.common.response.Cursor
import com.eatngo.extension.orThrow
import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.dto.OrderItemSnapshotDto
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.infra.OrderPersistence
import com.github.f4b6a3.tsid.TsidCreator
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OrderService(
    private val orderPersistence: OrderPersistence,
) {
    fun createOrder(
        storeId: Long,
        customerId: Long,
        pickupDateTime: LocalDateTime,
        nickname: String,
        orderItemSnapshotDtos: List<OrderItemSnapshotDto>,
    ): Order {
        val order: Order =
            Order.create(
                orderNumber = TsidCreator.getTsid().toLong(),
                customerId = customerId,
                storeId = storeId,
                pickupDateTime = pickupDateTime,
                nickname = nickname,
                orderItems =
                    orderItemSnapshotDtos.map {
                        with(it) {
                            OrderItem.of(
                                productId = productId,
                                name = productName,
                                quantity = quantity,
                                originPrice = originPrice,
                                finalPrice = finalPrice,
                                imageUrl = imageUrl,
                            )
                        }
                    },
            )

        return orderPersistence.save(order)
    }

    fun getById(orderId: Long): Order = orderPersistence.findById(orderId).orThrow { OrderException.OrderNotFound(orderId) }

    fun update(order: Order): Order = orderPersistence.update(order)

    fun findAllByQueryParam(queryParam: OrderQueryParamDto): Cursor<Order> = orderPersistence.findAllByQueryParameter(queryParam)
}
