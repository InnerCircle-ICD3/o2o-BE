package com.eatngo.inventory.event

interface StockEventPublisher {
    fun publishDecreaseEvent(stockEvent: StockEvent)
    fun publishSoldOutEvent(stockEvent: StockEvent)
}