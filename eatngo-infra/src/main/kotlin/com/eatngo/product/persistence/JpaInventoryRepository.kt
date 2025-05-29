package com.eatngo.product.persistence

import com.eatngo.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaInventoryRepository : JpaRepository<ProductEntity, Long> {
}