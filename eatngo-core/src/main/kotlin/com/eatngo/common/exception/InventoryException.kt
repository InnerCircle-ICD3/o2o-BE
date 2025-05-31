package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

open class InventoryException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {

    class InventoryNotFound(productId: Long) : InventoryException(
        BusinessErrorCode.INVENTORY_NOT_FOUND,
        "${BusinessErrorCode.INVENTORY_NOT_FOUND.message} (ID: $productId)",
        mapOf("productId" to productId)
    )

}