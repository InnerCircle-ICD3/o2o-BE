package com.eatngo.inventory.persistence

import com.eatngo.inventory.entity.InventoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.*

interface JpaInventoryRepository : JpaRepository<InventoryEntity, Long> {
    fun findTopByProductIdAndInventoryDateOrderByVersionDesc(productId: Long, localDate: LocalDate): Optional<InventoryEntity>
    fun deleteByProductId(productId: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE InventoryEntity i
           SET i.stock = :stock
         WHERE i.productId = :productId
         AND i.inventoryDate = :LocalDate
        """
    )
    fun updateStock(
        @Param("productId") productId: Long,
        @Param("stock") stock: Int,
        localDate: LocalDate
    ): Int

    fun findAllByProductIdInAndInventoryDate(productIds: List<Long>, localDate: LocalDate): List<InventoryEntity>
}