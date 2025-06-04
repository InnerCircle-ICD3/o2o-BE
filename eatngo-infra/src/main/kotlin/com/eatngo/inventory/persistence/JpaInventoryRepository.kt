package com.eatngo.inventory.persistence

import com.eatngo.inventory.entity.InventoryEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface JpaInventoryRepository : JpaRepository<InventoryEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
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

    fun findAllByProductIdIn(productIds: List<Long>): List<InventoryEntity>

//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Query(
//        """
//        UPDATE InventoryEntity i
//            SET i.quantity = :quantity,
//                i.stock = :stock
//         WHERE i.productId = :productId
//        """
//    )
//    fun updateInventory(
//        @Param("quantity") quantity: Int,
//        @Param("stock") stock: Int,
//        @Param("productId") productId: Long
//    )
}