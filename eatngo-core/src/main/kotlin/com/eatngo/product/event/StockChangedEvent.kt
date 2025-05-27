package com.eatngo.product.event

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