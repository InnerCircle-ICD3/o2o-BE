package com.eatngo.common.exception.product

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level

open class StockException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    class StockNotFound(productId: Long) : StockException(
        errorCode =BusinessErrorCode.STOCK_NOT_FOUND,
        message =  "${BusinessErrorCode.STOCK_NOT_FOUND.message} (ID: $productId)",
        data =     mapOf("productId" to productId)
    )

    class StockEmpty(productId: Long) : StockException(
        errorCode =BusinessErrorCode.STOCK_EMPTY,
        message =  "${BusinessErrorCode.STOCK_EMPTY.message} (ID: $productId)",
        data =     mapOf("productId" to productId)
    )

}