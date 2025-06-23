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

    class StoreProductCountException(storeId: Long) : ProductException(
        errorCode = BusinessErrorCode.STORE_PRODUCT_COUNT_EXCEPTION,
        message = "${BusinessErrorCode.STORE_PRODUCT_COUNT_EXCEPTION.message} (ID: $storeId)",
        data = mapOf("storeId" to storeId)
    )

    class ProductTypeDuplicationException(storeId: Long) : ProductException(
        errorCode = BusinessErrorCode.PRODUCT_TYPE_DUPLICATION_EXCEPTION,
        message = "${BusinessErrorCode.PRODUCT_TYPE_DUPLICATION_EXCEPTION.message} (ID: $storeId)",
        data = mapOf("storeId" to storeId)
    )
}