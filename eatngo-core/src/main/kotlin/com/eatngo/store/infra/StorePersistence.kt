package com.eatngo.store.infra

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store

/**
 * 매장 영속성 인터페이스
 */
interface StorePersistence {
    /**
     * ID로 매장 조회
     */
    suspend fun findById(id: Long): Store?

    /**
     * ID들로 매장들 조회
     */
    suspend fun findAllByIds(storeIds: List<String>): List<Store>

    /**
     * 점주 ID로 매장 목록 조회
     */
    suspend fun findByOwnerId(ownerId: String): Store?

    /**
     * 매장 저장
     */
    suspend fun save(store: Store): Store
    
    /**
     * 매장 삭제 (softDelete)
     */
    suspend fun softDelete(id: Long): Boolean
    
    /**
     * 매장 상태 업데이트
     */
    suspend fun updateStatus(id: Long, status: StoreEnum.StoreStatus): Boolean

} 