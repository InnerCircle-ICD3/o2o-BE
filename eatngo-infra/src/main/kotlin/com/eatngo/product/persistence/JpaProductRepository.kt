package com.eatngo.product.persistence

import com.eatngo.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface JpaProductRepository : JpaRepository<ProductEntity, Long> {
    fun findAllByStoreId(storeId: Long): List<ProductEntity>
    fun findByIdAndStoreId(productId: Long, storeId: Long): Optional<ProductEntity>
}