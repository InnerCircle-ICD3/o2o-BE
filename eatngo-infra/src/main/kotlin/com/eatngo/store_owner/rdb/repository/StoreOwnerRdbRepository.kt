package com.eatngo.store_owner.rdb.repository

import com.eatngo.store_owner.rdb.entity.StoreOwnerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface StoreOwnerRdbRepository : JpaRepository<StoreOwnerJpaEntity, Long> {
    fun findByAccount_Id(userId: Long): StoreOwnerJpaEntity?


    @Query(
        """
            SELECT s FROM StoreOwnerJpaEntity s
            WHERE s.id = :id
        """
    )
    override fun findById(id: Long): Optional<StoreOwnerJpaEntity>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
            UPDATE StoreOwnerJpaEntity s
            SET s.deletedAt = CURRENT_TIMESTAMP
            WHERE s.id = :id
    """
    )
    fun softDeleteById(id: Long)
}