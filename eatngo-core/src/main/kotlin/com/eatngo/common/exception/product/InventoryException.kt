package com.eatngo.common.exception.product

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level

open class InventoryException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    class InventoryNotFound(productId: Long) : InventoryException(
        errorCode = BusinessErrorCode.INVENTORY_NOT_FOUND,
        message = "${BusinessErrorCode.INVENTORY_NOT_FOUND.message} (ID: $productId)",
        data = mapOf("productId" to productId)
    )

}