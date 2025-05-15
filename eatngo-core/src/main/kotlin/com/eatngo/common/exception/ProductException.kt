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
    class ProductNotFound(menuId: String) : ProductException(
        BusinessErrorCode.PRODUCT_NOT_FOUND,
        "${BusinessErrorCode.PRODUCT_NOT_FOUND.message} (ID: $menuId)",
        mapOf("menuId" to menuId)
    )

    class ProductNotAvailable(menuId: String) : ProductException(
        BusinessErrorCode.PRODUCT_NOT_AVAILABLE,
        "${BusinessErrorCode.PRODUCT_NOT_AVAILABLE.message} (ID: $menuId)",
        mapOf("menuId" to menuId)
    )

    class ProductSoldOut(menuId: String) : ProductException(
        BusinessErrorCode.PRODUCT_SOLD_OUT,
        "${BusinessErrorCode.PRODUCT_SOLD_OUT.message} (ID: $menuId)",
        mapOf("menuId" to menuId)
    )
}