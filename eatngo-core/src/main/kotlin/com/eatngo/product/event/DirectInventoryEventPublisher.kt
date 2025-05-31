package com.eatngo.product.event

import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventoryEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class DirectInventoryEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) : InventoryEventPublisher {

    override fun publishInventoryChangedEvent(
        productId: Long,
        quantity: Int,
        stock: Int
    ) {
        eventPublisher.publishEvent(
            InventoryChangedEvent(
                productId = productId,
                quantity = quantity,
                stock = stock
            )
        )
    }

}