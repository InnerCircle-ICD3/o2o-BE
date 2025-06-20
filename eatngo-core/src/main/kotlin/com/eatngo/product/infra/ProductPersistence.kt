package com.eatngo.product.infra

import com.eatngo.product.domain.Product

interface ProductPersistence {
    fun save(product: Product): Product
    fun findActivatedProductById(productId: Long): Product?
    fun findAllActivatedProductByStoreId(storeId: Long): List<Product>
    fun findAllActivatedProductsByStoreIds(storeIds: List<Long>): List<Product>
    fun findActivatedProductByIdAndStoreId(productId: Long, storeId: Long): Product?
    fun countActiveProductsByStoreId(storeId: Long): Long
}