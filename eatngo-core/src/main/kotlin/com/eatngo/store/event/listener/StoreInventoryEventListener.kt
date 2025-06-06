package com.eatngo.store.event.listener

import com.eatngo.common.constant.StoreEnum
import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventoryChangedType
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.store.event.StoreStatusChangedEvent
import com.eatngo.store.service.StoreService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 재고 이벤트를 듣고 매장 상태를 관리하는 리스너
 */
@Component
@Transactional
class StoreInventoryEventListener(
    private val storeService: StoreService,
    private val productPersistence: ProductPersistence,
    private val eventPublisher: ApplicationEventPublisher
) {

    @EventListener
    fun handleInventoryChangedEvent(event: InventoryChangedEvent) {
            val product = productPersistence.findActivatedProductById(event.productId) ?: return
            
            val store = storeService.getStoreById(product.storeId)
            val previousStatus = store.status
            
            val newStatus = when (event.inventoryChangedType) {
                InventoryChangedType.OUT_OF_STOCK -> {
                    StoreEnum.StoreStatus.CLOSED
                }
                InventoryChangedType.RESTOCKED, 
                InventoryChangedType.IN_STOCK,
                InventoryChangedType.ADEQUATE_STOCK -> {
                    StoreEnum.StoreStatus.OPEN
                }
                InventoryChangedType.LOW_STOCK -> {
                    return // 상태 변경하지 않음
                }
            }
            
            // 시스템에 의한 상태 변경 (storeOwnerId = null)
            val updatedStore = storeService.updateStoreStatus(store.id, newStatus, null)
            
            // 상태가 실제로 변경된 경우에만 이벤트 발행 - 검색용
            if (updatedStore.status != previousStatus) {
                eventPublisher.publishEvent(
                    StoreStatusChangedEvent(
                        storeId = updatedStore.id,
                        userId = 0L, // 시스템에 의한 변경
                        previousStatus = previousStatus,
                        currentStatus = updatedStore.status
                    )
                )
            }
    }
} 