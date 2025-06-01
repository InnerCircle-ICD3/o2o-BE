package com.eatngo.inventory.event

import com.eatngo.inventory.dto.StockDto

interface StockEventPublisher {
    fun publishDecreaseEvent(stockDto: StockDto)
    fun publishSoldOutEvent(stockDto: StockDto)
}