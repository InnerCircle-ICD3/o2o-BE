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

    /**
     * 스케줄러용 매장 조회 - 성능 최적화를 위한 1차 필터링
     *
     * 시간 윈도우 기반 대략적 후보 조회:
     * - closeTime이 startTime ~ endTime 범위에 있는 OPEN 상태 매장들
     * - 도메인 레벨에서 정확한 재검증이 필요 (isPickupTimeEnded)
     *
     * @param dayOfWeek 요일 (MONDAY, TUESDAY, ...)
     * @param startTime 윈도우 시작 시간 (현재시간 - 31분)
     * @param endTime 윈도우 종료 시간 (현재시간 - 1분)
     */
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
