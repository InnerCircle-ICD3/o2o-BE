package com.eatngo.subscription.rdb.repository

import com.eatngo.subscription.rdb.entity.StoreSubscriptionJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StoreSubscriptionJpaRepository : JpaRepository<StoreSubscriptionJpaEntity, Long> {
    /**
     * 사용자 ID로 구독 목록 조회
     */
    @Query(value = "SELECT s FROM StoreSubscriptionJpaEntity s WHERE s.userId = :userId AND s.deletedAt IS NULL")
    fun findByUserId(userId: Long): List<StoreSubscriptionJpaEntity>

    /**
     * 매장 ID로 구독 목록 조회
     */
    @Query("SELECT s FROM StoreSubscriptionJpaEntity s WHERE s.storeId = :storeId AND s.deletedAt IS NULL")
    fun findByStoreId(storeId: Long): List<StoreSubscriptionJpaEntity>

    /**
     * 사용자 ID와 매장 ID로 구독 조회
     */
    fun findByUserIdAndStoreId(userId: Long?, storeId: Long): StoreSubscriptionJpaEntity?

    /**
     * 사용자 ID와 매장 ID로 구독 존재 여부 확인
     */
    fun existsByUserIdAndStoreId(userId: Long, storeId: Long): Boolean

    /**
     * 매장 ID 리스트로 구독 정보 일괄 조회
     */
    @Query("SELECT s FROM StoreSubscriptionJpaEntity s WHERE s.storeId IN :storeIds AND s.deletedAt IS NULL")
    fun findAllByStoreIds(@Param("storeIds") storeIds: List<Long>): List<StoreSubscriptionJpaEntity>

    /**
     * 사용자 ID로 매장 ID 목록 조회
     */
    @Query("SELECT s.storeId FROM StoreSubscriptionJpaEntity s WHERE s.userId = :userId AND s.deletedAt IS NULL")
    fun findStoreIdsByUserId(@Param("userId") userId: Long): List<Long>

    /**
     * 고객용 구독 목록 커서 기반 조회 (무한 스크롤)
     */
    @Query("""
        SELECT s FROM StoreSubscriptionJpaEntity s 
        WHERE s.userId = :userId 
        AND s.deletedAt IS NULL 
        AND (:lastId IS NULL OR s.id < :lastId)
        ORDER BY s.id DESC
    """)
    fun cursoredFindByUserId(
        @Param("userId") userId: Long,
        @Param("lastId") lastId: Long?,
        pageable: Pageable
    ): Slice<StoreSubscriptionJpaEntity>

    /**
     * 점주용 구독자 목록 커서 기반 조회 (무한 스크롤)
     */
    @Query("""
        SELECT s FROM StoreSubscriptionJpaEntity s 
        WHERE s.storeId = :storeId 
        AND s.deletedAt IS NULL 
        AND (:lastId IS NULL OR s.id < :lastId)
        ORDER BY s.id DESC
    """)
    fun cursoredFindByStoreId(
        @Param("storeId") storeId: Long,
        @Param("lastId") lastId: Long?,
        pageable: Pageable
    ): Slice<StoreSubscriptionJpaEntity>
}