package com.eatngo.product.service

import com.eatngo.common.exception.product.ProductException.ProductTypeDuplicationException
import com.eatngo.common.exception.product.ProductException.StoreProductCountException
import com.eatngo.product.domain.Product
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
        validateTotalProductCountOverInStore(storeId, products)
        validateDuplicateProductTypeInStore(storeId, products)
    }

    private fun validateTotalProductCountOverInStore(storeId: Long, products: List<Product>) {
        if (products.size > 3) {
            throw StoreProductCountException(storeId = storeId)
        }
    }

    private fun validateDuplicateProductTypeInStore(storeId: Long, products: List<Product>) {
        val sizeCountMap: Map<ProductSizeType, Int> = products.groupingBy { it.getSize() }.eachCount()

        if (hasDuplicateProductSize(sizeCountMap)) {
            throw ProductTypeDuplicationException(storeId = storeId)
        }
    }

    private fun hasDuplicateProductSize(sizeCountMap: Map<ProductSizeType, Int>) =
        sizeCountMap.filterValues { it > 1 }.isNotEmpty()
}