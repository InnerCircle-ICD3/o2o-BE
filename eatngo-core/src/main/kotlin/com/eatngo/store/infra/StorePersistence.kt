package com.eatngo.store.infra

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.Address
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
     * 점주 ID로 매장 목록 조회
     */
    suspend fun findByOwnerId(ownerId: String): Store?
    
    /**
     * 위치 기반 매장 검색
     * @param address 주소
     * @param radiusKm 검색 반경 (km)
     * @param limit 결과 제한
     * @param offset 오프셋
     */
    suspend fun findNearby(address: Address, radiusKm: Double, limit: Int, offset: Int): List<Store>
    
    /**
     * 이름 기반 매장 검색
     */
    suspend fun findByNameContaining(name: String, limit: Int, offset: Int): List<Store>
    
    /**
     * 카테고리 기반 매장 검색
     */
    suspend fun findByCategory(category: String, limit: Int, offset: Int): List<Store>
    
    /**
     * 현재 영업 중인 매장 조회 (매장 상태가 OPEN인 매장)
     */
    suspend fun findOpenStores(limit: Int, offset: Int): List<Store>
    
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
    
    /**
     * 삭제되지 않은 매장만 조회
     */
    fun excludeDeleted(stores: List<Store>): List<Store> {
        return stores.filter { it.deletedAt == null }
    }
} 