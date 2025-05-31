package com.eatngo.product.event

import com.eatngo.common.exception.ProductException.ProductNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventoryChangedType
import com.eatngo.inventory.event.InventoryEventPublisher
import com.eatngo.inventory.event.StockEventPublisher
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.event.OrderCanceledEvent
import com.eatngo.order.event.OrderCreatedEvent
import com.eatngo.order.event.OrderEvent
import com.eatngo.product.domain.Product
import com.eatngo.product.infra.ProductPersistence
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ProductEventListener(
    private val inventoryCachePersistence: InventoryCachePersistence,
    private val inventoryEventPublisher: InventoryEventPublisher,
    private val inventoryPersistence: InventoryPersistence,
    private val productPersistence: ProductPersistence,
    private val stockEventPublisher: StockEventPublisher
) {

    @EventListener
    fun handleOrderEvent(event: OrderEvent) {
        when (event) {
            is OrderCreatedEvent -> {
                for (orderItem in event.order.orderItems) {
                    processStockDecreaseTask(orderItem)
                }
            }

            is OrderCanceledEvent -> {
                // TODO
            }
        }

    }

    private fun processStockDecreaseTask(orderItem: OrderItem) {
        inventoryCachePersistence.decreaseStock(orderItem.productId, orderItem.quantity)
        publishEvent(orderItem)
    }

    private fun processStockRollbackTask(orderItem: OrderItem) {
        // TODO 다음 PR 재고 롤백 기능 호출
        publishEvent(orderItem)
    }

    private fun publishEvent(orderItem: OrderItem) {
        val storeId: Long = productPersistence.findById(orderItem.productId)
            .orThrow { ProductNotFound(orderItem.productId) }
            .storeId
        val allProducts: List<Product> = productPersistence.findAllByStoreId(storeId)
        val inventoryDtos = allProducts.map { p -> inventoryCachePersistence.findByProductId(p.id) }

        if (inventoryDtos.isEmpty()) {
            inventoryEventPublisher.publishInventoryChangedEvent(
                InventoryChangedEvent(
                    productId = orderItem.productId,
                    inventoryChangedType = InventoryChangedType.OUT_OF_STOCK
                )
            )
        }

        val totalStock = inventoryDtos.sumOf { it?.stock ?: 0 }
        if (totalStock == 0) {
            inventoryEventPublisher.publishInventoryChangedEvent(
                InventoryChangedEvent(
                    productId = orderItem.productId,
                    inventoryChangedType = InventoryChangedType.OUT_OF_STOCK
                )
            )
        } else if (totalStock <= 3) {
            inventoryEventPublisher.publishInventoryChangedEvent(
                InventoryChangedEvent(
                    productId = orderItem.productId,
                    inventoryChangedType = InventoryChangedType.LOW_STOCK
                )
            )
        } else if (totalStock >= 5) {
            inventoryEventPublisher.publishInventoryChangedEvent(
                InventoryChangedEvent(
                    productId = orderItem.productId,
                    inventoryChangedType = InventoryChangedType.IN_STOCK
                )
            )
        }
    }

}