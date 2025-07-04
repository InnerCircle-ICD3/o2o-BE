package com.eatngo.inventory.persistence

import com.eatngo.inventory.entity.InventoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.*

interface JpaInventoryRepository : JpaRepository<InventoryEntity, Long> {
    fun findTopByProductIdAndInventoryDateOrderByVersionDescIdDesc(
        productId: Long,
        localDate: LocalDate
    ): Optional<InventoryEntity>

    @Query(
        """
        SELECT i FROM InventoryEntity i 
        WHERE i.productId IN :productIds 
        AND i.version = (
            SELECT MAX(i2.version) 
            FROM InventoryEntity i2 
            WHERE i2.productId = i.productId
        )
    """
    )
    fun findLatestByProductIds(@Param("productIds") productIds: List<Long>): List<InventoryEntity>

    fun deleteByProductId(productId: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE InventoryEntity i
           SET i.stock = :stock
         WHERE i.productId = :productId
         AND i.inventoryDate = :localDate
        """
    )
    fun updateStock(
        @Param("productId") productId: Long,
        @Param("stock") stock: Int,
        @Param("localDate") localDate: LocalDate
    ): Int

    fun findAllByProductIdInAndInventoryDate(productIds: List<Long>, localDate: LocalDate): List<InventoryEntity>
}