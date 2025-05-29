package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

/**
 * 상품 관련 예외
 */
open class ProductException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {

    // 상품 관련 예외
    class ProductNotFound(productId: Long) : ProductException(
        BusinessErrorCode.PRODUCT_NOT_FOUND,
        "${BusinessErrorCode.PRODUCT_NOT_FOUND.message} (ID: $productId)",
        mapOf("menuId" to productId)
    )

    class ProductNotAvailable(productId: Long) : ProductException(
        BusinessErrorCode.PRODUCT_NOT_AVAILABLE,
        "${BusinessErrorCode.PRODUCT_NOT_AVAILABLE.message} (ID: $productId)",
        mapOf("menuId" to productId)
    )

    class ProductSoldOut(productId: Long) : ProductException(
        BusinessErrorCode.PRODUCT_SOLD_OUT,
        "${BusinessErrorCode.PRODUCT_SOLD_OUT.message} (ID: $productId)",
        mapOf("menuId" to productId)
    )
}