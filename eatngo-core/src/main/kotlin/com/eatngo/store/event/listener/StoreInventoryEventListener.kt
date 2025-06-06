package com.eatngo.store.event.listener

import com.eatngo.common.constant.StoreEnum
import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventoryChangedType
import com.eatngo.inventory.event.StockChangedEvent
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
        try {
            val product = productPersistence.findActivatedProductById(event.productId) ?: return
            
            val store = storeService.getStoreById(product.storeId)
            val previousStatus = store.status
            
            val newStatus = when (event.inventoryChangedType) {
                InventoryChangedType.OUT_OF_STOCK -> {
                    log.info("재고 소진 감지 - 매장 {} 닫기", store.id)
                    StoreEnum.StoreStatus.CLOSED
                }
                InventoryChangedType.RESTOCKED, 
                InventoryChangedType.IN_STOCK,
                InventoryChangedType.ADEQUATE_STOCK -> {
                    log.info("재고 복구 감지 - 매장 {} 열기", store.id)
                    StoreEnum.StoreStatus.OPEN
                }
                InventoryChangedType.LOW_STOCK -> {
                    log.info("재고 부족 경고 - 매장 {} 상태 유지", store.id)
                    return // 상태 변경하지 않음
                }
            }
            
            // 시스템에 의한 상태 변경 (storeOwnerId = null)
            val updatedStore = storeService.updateStoreStatus(store.id, newStatus, null)
            
            // 상태가 실제로 변경된 경우에만 이벤트 발행
            if (updatedStore.status != previousStatus) {
                // 매장 상태 변경 이벤트 발행
                eventPublisher.publishEvent(
                    StoreStatusChangedEvent(
                        storeId = updatedStore.id,
                        userId = 0L, // 시스템에 의한 변경
                        previousStatus = previousStatus,
                        currentStatus = updatedStore.status
                    )
                )
            }
            
        } catch (e: Exception) {
            log.error("재고 변경 이벤트 처리 중 오류 발생: productId={}", event.productId, e)
        }
    }

    @EventListener
    fun handleStockChangedEvent(event: StockChangedEvent) {
        try {
            val product = productPersistence.findActivatedProductById(event.productId) ?: return
            
            val store = storeService.getStoreById(product.storeId)
            
            log.info("재고 변경 이벤트 수신: 매장={}, 상품={}, 결과={}, 수량={}", 
                store.id, event.productId, event.resultStatus, event.quantity)
            
        } catch (e: Exception) {
            log.error("재고 변경 이벤트 처리 중 오류 발생: productId={}", event.productId, e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StoreInventoryEventListener::class.java)
    }
} 