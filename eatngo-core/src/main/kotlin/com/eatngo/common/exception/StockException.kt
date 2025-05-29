package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

open class StockException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null
) : RuntimeException(message) {

    class StockNotFound(productId: Long) : StockException(
        BusinessErrorCode.STOCK_NOT_FOUND,
        "${BusinessErrorCode.STOCK_NOT_FOUND.message} (ID: $productId)",
        mapOf("productId" to productId)
    )

    class StockEmpty(productId: Long) : StockException(
        BusinessErrorCode.STOCK_EMPTY,
        "${BusinessErrorCode.STOCK_EMPTY.message} (ID: $productId)",
        mapOf("productId" to productId)
    )

}