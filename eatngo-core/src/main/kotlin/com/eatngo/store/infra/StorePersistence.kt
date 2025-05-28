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
    * 매장 삭제 (soft delete)
    * @param id 삭제할 매장 ID
    * @return 삭제 성공 여부
    */
    fun deleteById(id: Long): Boolean
}