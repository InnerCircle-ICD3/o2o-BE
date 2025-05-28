package com.eatngo.store.service

import com.eatngo.store.domain.Store
import com.eatngo.store.dto.*

/**
 * 상점 서비스 인터페이스
 */
interface StoreService {
    /**
     * 상점 생성
     */
    fun createStore(request: StoreCreateDto): Store
    
    /**
     * 상점 정보 수정
     */
    fun updateStore(id: Long, request: StoreUpdateDto): Store

    /**
     * 상점 상태 변경 (시스템)
     */
    fun updateStoreStatus(id: Long, hasStock: Boolean): Store

    /**
     * 상점 상태 변경 (점주)
     */
    fun updateStoreStatus(id: Long, newStatus: String, storeOwnerId: Long): Store

    /**
     * 상점 픽업 정보 변경
     */
    fun updateStorePickupInfo(id: Long, request: PickUpInfoDto, storeOwnerId: Long): Store

    /**
     * 상점 삭제 (Soft Delete)
     */
    fun deleteStore(id: Long, storeOwnerId: Long): Store

    /**
     * 상점 주인 ID로 상점 조회
     */
    fun getStoresByStoreOwnerId(storeOwnerId: Long): List<Store>

    /**
     * 상점 ID로 상점 조회
     */
    fun getStoreById(id: Long): Store
} 