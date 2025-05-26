package com.eatngo.product.infra

import com.eatngo.product.domain.Product

interface ProductCachePersistence {
    fun save(product: Product)
    fun findById(productId: Long): Product?
    fun findAllByStoreId(storeId: Long): List<Product>
}