package com.eatngo.product.infra

import com.eatngo.product.domain.Product

interface ProductPersistence {
    fun save(product: Product): Product
    fun findById(productId: Long): Product?
    fun findAllByStoreId(storeId: Long): List<Product>
    fun findByIdAndStoreId(productId: Long, storeId: Long): Product?
    fun updateStock(productId: Long, stockQuantity: Int): Int
}

fun ProductPersistence.findByIdOrThrow(productId: Long): Product {
    return this.findById(productId) ?: throw IllegalArgumentException("상품을 찾을 수 없습니다.")
}

fun ProductPersistence.findByIdAndStoreIdOrElseThrow(productId: Long, storeId: Long): Product {
    return this.findByIdAndStoreId(productId, storeId) ?: throw IllegalArgumentException("상품을 찾을 수 없습니다.")
}