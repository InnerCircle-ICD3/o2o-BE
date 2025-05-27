package com.eatngo.store.infra

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.StoreException
import com.eatngo.store.domain.Store

/**
 * 매장 영속성 인터페이스
 */
interface StorePersistence {
    /**
     * ID로 매장 조회
     */
    fun findById(id: Long): Store?

    /**
     * ID들로 매장들 조회
     */
    fun findAllByIds(storeIds: List<Long>): List<Store>

    /**
     * 점주 ID로 매장 목록 조회
     */
    fun findByOwnerId(storeOwnerId: Long): List<Store>

    /**
     * 매장 저장
     */
    fun save(store: Store): Store
    
    /**
     * 매장 삭제 (softDelete)
     */
    fun softDelete(id: Long): Boolean
    
    /**
     * 매장 상태 업데이트
     */
    fun updateStatus(id: Long, status: StoreEnum.StoreStatus): Boolean
}

// extensions
fun StorePersistence.findByIdOrThrow(id: Long): Store =
    this.findById(id) ?: throw StoreException.StoreNotFound(id)

fun Store.requireOwner(storeOwnerId: Long) {
    if (this.storeOwnerId != storeOwnerId) { throw StoreException.Forbidden(storeOwnerId) }
}