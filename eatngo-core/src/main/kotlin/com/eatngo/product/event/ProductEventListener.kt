package com.eatngo.product.event

import com.eatngo.common.exception.product.ProductException.ProductNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.event.OrderCanceledEvent
import com.eatngo.order.event.OrderCreatedEvent
import com.eatngo.order.event.OrderEvent
import com.eatngo.product.domain.Product
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.product.service.InventoryChangeNotifier
import com.eatngo.product.service.ProductService
import com.eatngo.store.event.StorePickupEndedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
@Transactional
class ProductEventListener(
    private val inventoryCachePersistence: InventoryCachePersistence,
    private val productPersistence: ProductPersistence,
    private val inventoryChangeNotifier: InventoryChangeNotifier,
    private val productService: ProductService,
    private val inventoryPersistence: InventoryPersistence,
) {
    @EventListener
    fun handleOrderEvent(event: OrderEvent) {
        when (event) {
            is OrderCreatedEvent -> event.order.orderItems.forEach { processStockDecrease(it) }
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
        val storeId =
            productPersistence
                .findActivatedProductById(orderItem.productId)
                .orThrow { ProductNotFound(orderItem.productId) }
                .storeId
        val initialStock = productService.findTotalInitialStocks(storeId)

        inventoryChangeNotifier.notifyInventoryStatusChange(
            storeId = storeId,
            productId = orderItem.productId,
            initialStock = initialStock,
        )
    }

    @EventListener
    fun handleStoreEvent(event: StorePickupEndedEvent) {
        val inventoryDate = calculateDailyInventoryCreationDate(event)
        event.closedStoreIds.forEach {
            val inventories: List<Inventory> = findStoreInventories(it, inventoryDate)
            inventoryPersistence.saveAll(inventories)
        }
    }

    private fun calculateDailyInventoryCreationDate(event: StorePickupEndedEvent): LocalDate = event.closedAt.plusDays(1).toLocalDate()

    private fun findStoreInventories(
        storeId: Long,
        inventoryDate: LocalDate,
    ): List<Inventory> {
        val products: List<Product> =
            productPersistence
                .findAllActivatedProductByStoreId(storeId)
                .orThrow { ProductNotFound(storeId) }
        // 캐시로 바꿀지 고민 필요! ~> 캐시로 변경 시 InventorySyncPublisher 호출 필요!
        val inventories: List<Inventory> =
            products.map { product ->
                Inventory(
                    quantity = 0,
                    stock = 0,
                    productId = product.id,
                    inventoryDate = inventoryDate,
                )
            }
        return inventories
    }
}
