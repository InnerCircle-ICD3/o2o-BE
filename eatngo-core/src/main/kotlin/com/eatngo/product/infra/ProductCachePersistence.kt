package com.eatngo.product.infra

import com.eatngo.product.domain.Product

interface ProductCachePersistence {
    fun save(product: Product)
    fun findById(productId: Long): Product?
    fun findAllByStoreId(storeId: Long): List<Product>
    fun deleteById(productId: Long)
    fun findStockById(productId: Long): Int
    fun increaseStock(productId: Long, quantity: Int): Int
    fun decreaseStock(productId: Long, quantity: Int): Int
}