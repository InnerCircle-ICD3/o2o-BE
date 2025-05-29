package com.eatngo.product.event

import com.eatngo.inventory.event.ResultStatus
import com.eatngo.inventory.event.StockChangedEvent
import com.eatngo.inventory.event.StockEventPublisher
import com.eatngo.inventory.dto.StockDto
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DirectStockEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) : StockEventPublisher {

    override fun publishDecreaseEvent(stockDto: StockDto) {
        eventPublisher.publishEvent(
            StockChangedEvent(
                ResultStatus.SUCCESS,
                stockDto.orderId,
                stockDto.productId,
                stockDto.quantity
            )
        )
    }

    override fun publishSoldOutEvent(stockDto: StockDto) {
        eventPublisher.publishEvent(
            StockChangedEvent(
                ResultStatus.FAIL,
                stockDto.orderId,
                stockDto.productId,
                stockDto.quantity
            )
        )
    }

}