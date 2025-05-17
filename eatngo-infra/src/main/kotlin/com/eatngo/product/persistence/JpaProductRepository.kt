package com.eatngo.product.persistence

import com.eatngo.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaProductRepository : JpaRepository<ProductEntity, Long> {
    fun findAllByStoreId(storeId: Long): List<ProductEntity>
}