package com.eatngo.store.infra

import com.eatngo.store.domain.Store
import java.time.LocalDateTime

/**
 * 매장 영속성 인터페이스
 */
interface StorePersistence {
    /**
     * ID로 매장 조회(주소포함)
     */
    fun findById(id: Long): Store?

    /**
     * ID들로 매장들 조회
     */
    fun findAllByIds(storeIds: List<Long>): List<Store>

    /**
     * 점주 ID로 매장 목록 조회(주소포함)
     */
    fun findByOwnerId(storeOwnerId: Long): List<Store>

    /**
     * updatedAt 기준으로 매장 목록 조회 (Search 동기화 용도)
     */
    fun findByUpdatedAt(pivotTime: LocalDateTime): List<Store>

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
