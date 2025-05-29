package com.eatngo.inventory.event

data class StockChangedEvent(
    val resultStatus: ResultStatus,
    val orderId: Long,
    val productId: Long,
    val quantity: Int
) {
}

enum class ResultStatus {
    SUCCESS,
    FAIL,
}