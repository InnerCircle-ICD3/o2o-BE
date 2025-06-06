package com.eatngo.store.event

import com.eatngo.common.exception.product.ProductException
import com.eatngo.extension.orThrow
import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventoryChangedType
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.store.usecase.StoreStatusAutoChangeUseCase
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 재고 변경 이벤트를 처리하는 리스너
 */
@Component
@Transactional
class InventoryEventListener(
    private val storeStatusAutoChangeUseCase: StoreStatusAutoChangeUseCase,
    private val productPersistence: ProductPersistence
) {

    @EventListener
    fun handleInventoryChangedEvent(event: InventoryChangedEvent) {
        // OUT_OF_STOCK, RESTOCKED 이벤트만 처리
        if (event.inventoryChangedType != InventoryChangedType.OUT_OF_STOCK &&
            event.inventoryChangedType != InventoryChangedType.RESTOCKED) {
            return
        }
        
        log.info("재고 변경 이벤트 수신: productId={}, type={}", event.productId, event.inventoryChangedType)
        
        // 상품 정보 조회
        val product = productPersistence.findActivatedProductById(event.productId)
            .orThrow { ProductException.ProductNotFound(event.productId) }

        // 재고 상태에 따라 매장 상태 변경
        val hasStock = event.inventoryChangedType == InventoryChangedType.RESTOCKED
        val storeDto = storeStatusAutoChangeUseCase.changeStatusByInventory(product.storeId, hasStock)
        
        log.info("매장 상태 자동 변경 완료: storeId={}, status={}", storeDto.storeId, storeDto.status)
    }
    
    companion object {
        private val log = LoggerFactory.getLogger(InventoryEventListener::class.java)
    }
} 