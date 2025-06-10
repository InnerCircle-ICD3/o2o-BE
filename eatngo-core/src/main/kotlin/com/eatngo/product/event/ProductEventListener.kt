package com.eatngo.product.event

import com.eatngo.common.exception.product.ProductException.ProductNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.event.OrderCanceledEvent
import com.eatngo.order.event.OrderReadyEvent
import com.eatngo.order.event.OrderEvent
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.product.service.InventoryChangeNotifier
import com.eatngo.product.service.ProductService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ProductEventListener(
    private val inventoryCachePersistence: InventoryCachePersistence,
    private val productPersistence: ProductPersistence,
    private val inventoryChangeNotifier: InventoryChangeNotifier,
    private val productService: ProductService
) {

    @EventListener
    fun handleOrderEvent(event: OrderEvent) {
        when (event) {
            is OrderReadyEvent -> event.order.orderItems.forEach { processStockDecrease(it) }
            is OrderCanceledEvent -> event.order.orderItems.forEach { processStockRollback(it) }
        }
    }

    private fun processStockDecrease(orderItem: OrderItem) {
        inventoryCachePersistence.decreaseStock(orderItem.productId, orderItem.quantity)
        publishInventoryChangeIfNeeded(orderItem)
    }

    private fun processStockRollback(orderItem: OrderItem) {
        inventoryCachePersistence.rollbackStock(orderItem.productId, orderItem.quantity)
        publishInventoryChangeIfNeeded(orderItem)
    }

    private fun publishInventoryChangeIfNeeded(orderItem: OrderItem) {
        val storeId = productPersistence.findActivatedProductById(orderItem.productId)
            .orThrow { ProductNotFound(orderItem.productId) }
            .storeId
        val initialStock = productService.findTotalInitialStocks(storeId)

        inventoryChangeNotifier.notifyInventoryStatusChange(
            storeId = storeId,
            productId = orderItem.productId,
            initialStock = initialStock
        )
    }

}