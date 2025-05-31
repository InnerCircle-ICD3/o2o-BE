package com.eatngo.inventory.persistence

import com.eatngo.inventory.entity.InventoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface JpaInventoryRepository : JpaRepository<InventoryEntity, Long> {
    fun findTopByProductIdOrderByVersionDesc(productId: Long): Optional<InventoryEntity>
    fun deleteByProductId(productId: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE InventoryEntity i
           SET i.stock = :stock
         WHERE i.productId = :productId
        """
    )
    fun updateStock(
        @Param("productId") productId: Long,
        @Param("stock") stock: Int
    ): Int
}