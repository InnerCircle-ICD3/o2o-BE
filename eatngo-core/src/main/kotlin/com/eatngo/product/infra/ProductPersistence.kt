package com.eatngo.product.infra

import com.eatngo.product.domain.Product

interface ProductPersistence {
    fun save(product: Product): Product
    fun findById(productId: Long): Product?
    fun findAllByStoreId(storeId: Long): List<Product>
}