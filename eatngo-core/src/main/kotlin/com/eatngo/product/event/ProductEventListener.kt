package com.eatngo.product.event

import com.eatngo.inventory.event.StockEventPublisher
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.event.OrderCanceledEvent
import com.eatngo.order.event.OrderCreatedEvent
import com.eatngo.order.event.OrderEvent
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.product.infra.ProductPersistence
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ProductEventListener(
    private val inventoryCachePersistence: InventoryCachePersistence,
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

    fun processStockDecreaseTask(orderItem: OrderItem) {
        inventoryCachePersistence.decreaseStock(orderItem.productId, orderItem.quantity)
        // TODO 재고 정보 search 및 market 에게 알려주기 event 기반 -> 이벤트 한번에 모아 보낼지 따로 따로 보낼지도 고민 필요!
    }

    fun processStockRollbackTask(orderItem: OrderItem) {
        // TODO 다음 PR 재고 롤백 기능 호출
        // TODO market 및 search 에게 event 보내기
    }

    /**
     * 이전 버전 잠시 관리
     */
//    @CachePut("inventory", key = "#orderItem.productId")
//    fun decreaseStock(
//        orderItem: OrderItem
//    ): InventoryDto {
////        productCachePersistence.decreaseStock(orderItem.productId, orderItem.quantity)
//        // TODO 재고 정보 search 및 market 에게 알려주기 event 기반 -> 이벤트 한번에 모아 보낼지 따로 따로 보낼지도 고민 필요!
////        stockEventPublisher.~~
//        return InventoryDto(
//            quantity = TODO(),
//            stock = TODO()
//        )
//    }
//
//    @CachePut("inventory", key = "#orderItem.productId")
//    fun rollbackStock(
//        orderItem: OrderItem
//    ): InventoryDto {
////        stockEventPublisher.~~
//        return InventoryDto(
//            quantity = TODO(),
//            stock = TODO()
//        )
//    }

}