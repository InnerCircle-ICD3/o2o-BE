package com.eatngo.product.event

import com.eatngo.inventory.event.ResultStatus
import com.eatngo.inventory.event.StockChangedEvent
import com.eatngo.inventory.event.StockEventPublisher
import com.eatngo.inventory.event.StockEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class DirectStockEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) : StockEventPublisher {

    override fun publishDecreaseEvent(stockEvent: StockEvent) {
        eventPublisher.publishEvent(
            StockChangedEvent(
                ResultStatus.SUCCESS,
                stockEvent.orderId,
                stockEvent.productId,
                stockEvent.quantity
            )
        )
    }

    override fun publishSoldOutEvent(stockEvent: StockEvent) {
        eventPublisher.publishEvent(
            StockChangedEvent(
                ResultStatus.FAIL,
                stockEvent.orderId,
                stockEvent.productId,
                stockEvent.quantity
            )
        )
    }

}