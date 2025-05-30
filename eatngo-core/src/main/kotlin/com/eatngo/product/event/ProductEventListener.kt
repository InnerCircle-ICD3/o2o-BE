package com.eatngo.product.event

import com.eatngo.inventory.event.StockEventPublisher
import com.eatngo.order.event.OrderCanceledEvent
import com.eatngo.order.event.OrderCreatedEvent
import com.eatngo.order.event.OrderEvent
import com.eatngo.product.infra.ProductCachePersistence
import com.eatngo.product.infra.ProductPersistence
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ProductEventListener(
    private val productCachePersistence: ProductCachePersistence,
    private val productPersistence: ProductPersistence,
    private val stockEventPublisher: StockEventPublisher
) {

    @EventListener
    fun handleOrderEvent(event: OrderEvent) {
        when (event) {
            is OrderCreatedEvent -> {
                for (orderItem in event.order.orderItems) {
                    productCachePersistence.decreaseStock(orderItem.productId, orderItem.quantity)
                    // TODO 재고 정보 search 및 market 에게 알려주기 event 기반
                }
            }

            is OrderCanceledEvent -> {
                // TODO
            }
        }

    }

}