package com.eatngo.store.event

import com.eatngo.common.exception.product.ProductException
import com.eatngo.extension.orThrow
import com.eatngo.inventory.event.ResultStatus
import com.eatngo.inventory.event.StockChangedEvent
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.store.usecase.StoreStatusAutoChangeUseCase
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 재고 변경 결과 이벤트를 처리하는 리스너
 */
@Component
@Transactional
class StockEventListener(
    private val storeStatusAutoChangeUseCase: StoreStatusAutoChangeUseCase,
    private val productPersistence: ProductPersistence
) {

    @EventListener
    fun handleStockChangedEvent(event: StockChangedEvent) {
        // 실패(재고 부족) 이벤트만 처리
        if (event.resultStatus != ResultStatus.FAIL) {
            return
        }
        
        log.info("재고 변경 실패 이벤트 수신: orderId={}, productId={}, quantity={}", 
            event.orderId, event.productId, event.quantity)
        
        // 상품 정보 조회
        val product = productPersistence.findActivatedProductById(event.productId)
            .orThrow { ProductException.ProductNotFound(event.productId) }

        // 재고 부족으로 매장 상태 변경
        val storeDto = storeStatusAutoChangeUseCase.changeStatusByInventory(product.storeId, hasStock = false)
        
        log.info("매장 상태 자동 변경 완료 (재고 부족): storeId={}, status={}", storeDto.storeId, storeDto.status)
    }
    
    companion object {
        private val log = LoggerFactory.getLogger(StockEventListener::class.java)
    }
}
