package com.eatngo.store.rdb.repository

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.rdb.entity.StoreJpaEntity
import com.eatngo.store.scheduler.StoreSchedulerProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Optional

interface StoreRdbRepository : JpaRepository<StoreJpaEntity, Long> {
    @Query(
        """
    select s from StoreJpaEntity s
    join fetch s.address
    where s.id = :id
    """,
    )
    override fun findById(id: Long): Optional<StoreJpaEntity>

    @Query(
        """
    select s from StoreJpaEntity s
    join fetch s.address
    where s.storeOwnerId = :storeOwnerId
    """,
    )
    fun findByStoreOwnerIdWithAddress(storeOwnerId: Long): List<StoreJpaEntity>

    @Query(
        """
    select s from StoreJpaEntity s
    join fetch s.address
    where s.id in :ids
    """,
    )
    fun findAllByIdIn(ids: List<Long>): List<StoreJpaEntity>

    @Query(
        """
    select s from StoreJpaEntity s
    join fetch s.address
    where s.updatedAt > :pivotTime
    """,
    )
    fun findByUpdatedAt(pivotTime: LocalDateTime): List<StoreJpaEntity>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE StoreJpaEntity s
        SET s.status = :status, s.updatedAt = CURRENT_TIMESTAMP
        WHERE s.id = :storeId
    """
    )
    fun updateStatus(storeId: Long, status: StoreEnum.StoreStatus): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE StoreJpaEntity s
        SET s.deletedAt = CURRENT_TIMESTAMP
        WHERE s.id = :id
    """,
    )
    fun softDeleteById(id: Long): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE StoreJpaEntity s
        SET s.status = 'CLOSED', s.updatedAt = CURRENT_TIMESTAMP
        WHERE s.id IN :storeIds AND s.status = 'OPEN'
    """,
    )
    fun batchUpdateStatusToClosed(storeIds: List<Long>): Int

    @Query(
        value = """
    SELECT s.id as id, s.business_hours as businessHours, s.status as status
    FROM store s
    WHERE s.status = 'OPEN'
    AND s.deleted_at IS NULL
    AND EXISTS (
        SELECT 1 FROM jsonb_array_elements(s.business_hours) AS bh
        WHERE bh->>'dayOfWeek' = :dayOfWeek
        AND (bh->>'closeTime')::time <= :endTime
        AND (bh->>'closeTime')::time >= :startTime
    )
    """,
        nativeQuery = true
    )
    fun findOpenStoresForScheduler(
        dayOfWeek: String,
        startTime: LocalTime,
        endTime: LocalTime
    ): List<StoreSchedulerProjection>
}
