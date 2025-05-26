package com.eatngo.product.infra

import com.eatngo.product.domain.Product

interface ProductCachePersistence {
    fun save(product: Product)
    fun findById(productId: Long): Product?
    fun findAllByStoreId(storeId: Long): List<Product>
    fun deleteById(productId: Long): Unit
    fun findStockById(productId: Long): Long
    fun increaseStock(productId: Long, quantity: Long): Long
    fun decreaseStock(product: Product, quantity: Long): Long
}