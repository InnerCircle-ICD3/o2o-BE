package com.eatngo.subscription.rdb.repository

import com.eatngo.subscription.rdb.entity.StoreSubscriptionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StoreSubscriptionJpaRepository : JpaRepository<StoreSubscriptionJpaEntity, Long> {
    /**
     * 사용자 ID로 구독 목록 조회
     */
    fun findByUserId(userId: Long): List<StoreSubscriptionJpaEntity>

    /**
     * 매장 ID로 구독 목록 조회
     */
    fun findByStoreId(storeId: Long): List<StoreSubscriptionJpaEntity>

    /**
     * 사용자 ID와 매장 ID로 구독 조회
     */
    fun findByUserIdAndStoreId(userId: Long, storeId: Long): StoreSubscriptionJpaEntity?

    /**
     * 사용자 ID와 매장 ID로 구독 존재 여부 확인
     */
    fun existsByUserIdAndStoreId(userId: Long, storeId: Long): Boolean

    /**
     * 매장 ID 리스트로 구독 정보 일괄 조회 (N+1 문제 해결)
     */
    @Query("SELECT s FROM StoreSubscriptionJpaEntity s WHERE s.storeId IN :storeIds")
    fun findAllByStoreIds(@Param("storeIds") storeIds: List<Long>): List<StoreSubscriptionJpaEntity>

    /**
     * 사용자 ID로 매장 ID 목록 조회
     */
    @Query("SELECT s.storeId FROM StoreSubscriptionJpaEntity s WHERE s.userId = :userId")
    fun findStoreIdsByUserId(@Param("userId") userId: Long): List<Long>
} 