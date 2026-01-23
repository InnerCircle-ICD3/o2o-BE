package com.eatngo.product.event

import com.eatngo.common.circuitbreaker.annotation.RedisCircuitBreaker
import com.eatngo.common.exception.product.ProductException.ProductNotFound
import com.eatngo.extension.orElse
import com.eatngo.extension.orThrow
import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.event.InventorySyncEvent
import com.eatngo.inventory.event.InventorySyncPublisher
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
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ProductEventListener(
    private val inventoryCachePersistence: InventoryCachePersistence,
    private val productPersistence: ProductPersistence,
    private val inventoryChangeNotifier: InventoryChangeNotifier,
    private val productService: ProductService,
    private val inventoryPersistence: InventoryPersistence,
    private val inventorySyncPublisher: InventorySyncPublisher,
) {
    companion object {
        private val log = LoggerFactory.getLogger(ProductEventListener::class.java)
    }

    /**
     * Redis 연산은 트랜잭션 외부에서 실행됩니다.
     * Redis 성공 시 changedProductIds에 추가되어 나중에 DB에 동기화됩니다.
     */
    @EventListener
    @RedisCircuitBreaker(
        name = "product-stock-decrease",
        fallbackMethod = "handleOrderEventFallback",
        failureThreshold = 3,
        timeoutMinutes = 1L,
    )
    fun handleOrderEvent(event: OrderEvent) {
        when (event) {
            is OrderCreatedEvent -> event.order.orderItems.forEach { processStockDecrease(it) }
            is OrderCanceledEvent -> event.order.orderItems.forEach { processStockRollback(it) }
        }
    }

    private fun processStockDecrease(orderItem: OrderItem) {
        inventoryCachePersistence.decreaseStock(orderItem.productId, orderItem.quantity)
        inventoryCachePersistence.addChangedProductId(orderItem.productId)
        publishInventoryChangeIfNeeded(orderItem)
    }

    private fun processStockRollback(orderItem: OrderItem) {
        inventoryCachePersistence.rollbackStock(orderItem.productId, orderItem.quantity)
        inventoryCachePersistence.addChangedProductId(orderItem.productId)
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

    /**
     * Redis 장애 시 DB fallback 처리
     * 이 메서드는 @Transactional이 적용되어 DB 연산의 원자성을 보장합니다.
     */
    @Transactional
    private fun handleOrderEventFallback(event: OrderEvent, ex: Exception) {
        log.warn("Redis stock operation failed, falling back to DB", ex)

        when (event) {
            is OrderCreatedEvent -> event.order.orderItems.forEach { processStockDecreaseFromDB(it) }
            is OrderCanceledEvent -> event.order.orderItems.forEach { processStockRollbackFromDB(it) }
        }
    }

    private fun processStockDecreaseFromDB(orderItem: OrderItem) {
        val today = LocalDate.now()
        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(orderItem.productId, today)
                .orElse {
                    inventoryPersistence.save(
                        Inventory(
                            quantity = 0,
                            stock = 0,
                            productId = orderItem.productId,
                            inventoryDate = today,
                        )
                    )
                }

        val changedInventory = inventory.decreaseStock(orderItem.quantity)
        inventoryPersistence.updateStock(
            productId = orderItem.productId,
            stockQuantity = changedInventory.stock,
            localDate = today
        )

        // Redis 복구 후 동기화를 위해 pendingSyncProductIds에 추가
        inventoryCachePersistence.addPendingSyncProductId(orderItem.productId)

        // 이벤트 발행 (기존 동작 유지)
        inventorySyncPublisher.publishEvent(InventorySyncEvent(orderItem.productId, changedInventory.stock))

        publishInventoryChangeIfNeeded(orderItem)
    }

    private fun processStockRollbackFromDB(orderItem: OrderItem) {
        val today = LocalDate.now()
        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(orderItem.productId, today)
                .orElse {
                    inventoryPersistence.save(
                        Inventory(
                            quantity = 0,
                            stock = 0,
                            productId = orderItem.productId,
                            inventoryDate = today,
                        )
                    )
                }

        val changedInventory = inventory.increaseStock(orderItem.quantity)
        inventoryPersistence.updateStock(
            productId = orderItem.productId,
            stockQuantity = changedInventory.stock,
            localDate = today
        )

        // Redis 복구 후 동기화를 위해 pendingSyncProductIds에 추가
        inventoryCachePersistence.addPendingSyncProductId(orderItem.productId)

        inventorySyncPublisher.publishEvent(InventorySyncEvent(orderItem.productId, changedInventory.stock))

        publishInventoryChangeIfNeeded(orderItem)
    }

    @EventListener
    @Transactional
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
