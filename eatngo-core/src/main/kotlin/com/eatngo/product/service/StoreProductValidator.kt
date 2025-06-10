package com.eatngo.product.service

import com.eatngo.product.domain.ProductSizeType
import com.eatngo.product.infra.ProductPersistence
import org.springframework.stereotype.Service

@Service
class StoreProductValidator(
    private val productPersistence: ProductPersistence
) {

    fun validateProduct(storeId: Long) {
        validateStoreProductSizeType(storeId)
    }

    private fun validateStoreProductSizeType(storeId: Long) {
        val products = productPersistence.findAllActivatedProductByStoreId(storeId)
        if (products.size > 3) {
            throw IllegalArgumentException("There is more than 3 products in the store")
        }

        val sizeCountMap: Map<ProductSizeType, Int> = products.groupingBy { it.getSize() }.eachCount()

        if (hasDuplicateProductSize(sizeCountMap)) {
            throw IllegalArgumentException("There are multiple products in the store")
        }
    }

    private fun hasDuplicateProductSize(sizeCountMap: Map<ProductSizeType, Int>) =
        sizeCountMap.filterValues { it > 1 }.isNotEmpty()
}