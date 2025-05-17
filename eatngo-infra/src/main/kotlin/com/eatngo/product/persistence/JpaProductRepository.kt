package com.eatngo.product.persistence

import com.eatngo.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface JpaProductRepository : JpaRepository<ProductEntity, Long> {
    fun findAllByStoreId(storeId: Long): List<ProductEntity>
    fun findByIdAndStoreId(productId: Long, storeId: Long): Optional<ProductEntity>
}