package com.eatngo.product.infra

import com.eatngo.product.domain.Product

interface ProductPersistence {
    fun save(product: Product): Product
    fun findActivatedProductById(productId: Long): Product?
    fun findAllActivatedProductByStoreId(storeId: Long): List<Product>
    fun findActivatedProductByIdAndStoreId(productId: Long, storeId: Long): Product?
}