package com.eatngo.product.event

import com.eatngo.product.dto.StockDto

interface StockEventPublisher {
    fun publishDecreaseEvent(stockDto: StockDto)
    fun publishSoldOutEvent(stockDto: StockDto)
}