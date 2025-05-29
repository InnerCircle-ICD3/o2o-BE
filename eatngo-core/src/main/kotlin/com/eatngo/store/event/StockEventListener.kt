package com.eatngo.store.event

import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Component


/**
 * 매장 서비스에서 재고(Stock) 품절 이벤트를 리슨하여 매장 상태를 변경하는 리스너
 */
@Component
class StockEventListener(
    private val storeService: StoreService
) {
//    @Transactional
//    @EventListener
//    fun handleStockChangedEvent(event: StockChangedEvent) {
//        // 품절 이벤트만 처리
//        if (event.resultStatus == ResultStatus.FAIL) {
//
//            val storeId = getStoreIdByProductId(event.productId)
//            storeService.updateStoreStatus(storeId, hasStock = false)
//            //TODO: 매장 상태 업데이트 시 프론트에서 화면 재조회하도록 프론트 알림(WebSocket, SSE 등) 추가 해야 함
//        }
//    }
//
//    fun getStoreIdByProductId(productId: Long): Long {
//        TODO("Implement mapping logic")
//    }
}
