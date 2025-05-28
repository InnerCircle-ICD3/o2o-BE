package com.eatngo.store.rdb.repository

import com.eatngo.store.rdb.entity.StoreJpaEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface StoreRdbRepository : JpaRepository<StoreJpaEntity, Long> {
    @Query("SELECT s FROM StoreJpaEntity s WHERE s.id = :id")
    override fun findById(id: Long): Optional<StoreJpaEntity>

    @Query("SELECT s FROM StoreJpaEntity s WHERE s.id IN :ids")
    fun findAllByIds(ids: List<Long>): List<StoreJpaEntity>

    @Query("SELECT s FROM StoreJpaEntity s WHERE s.storeOwnerId = :storeOwnerId")
    fun findByStoreOwnerId(storeOwnerId: Long): List<StoreJpaEntity>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE StoreJpaEntity s
        SET s.deletedAt = CURRENT_TIMESTAMP
        WHERE s.id = :id
    """
    )
    fun softDeleteById(id: Long): Int
}