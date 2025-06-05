package com.eatngo.common.exception.product

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level

/**
 * 상품 관련 예외
 */
open class ProductException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    // 상품 관련 예외
    class ProductNotFound(productId: Long) : ProductException(
        errorCode = BusinessErrorCode.PRODUCT_NOT_FOUND,
        message = "${BusinessErrorCode.PRODUCT_NOT_FOUND.message} (ID: $productId)",
        data = mapOf("productId" to productId)
    )

    class ProductNotAvailable(productId: Long) : ProductException(
        errorCode = BusinessErrorCode.PRODUCT_NOT_AVAILABLE,
        message = "${BusinessErrorCode.PRODUCT_NOT_AVAILABLE.message} (ID: $productId)",
        data = mapOf("productId" to productId)
    )

    class ProductSoldOut(productId: Long) : ProductException(
        errorCode = BusinessErrorCode.PRODUCT_SOLD_OUT,
        message = "${BusinessErrorCode.PRODUCT_SOLD_OUT.message} (ID: $productId)",
        data = mapOf("productId" to productId)
    )
}