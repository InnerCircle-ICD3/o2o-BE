package com.eatngo.store.service

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.dto.*
import java.time.LocalDateTime

/**
 * 상점 서비스 인터페이스
 */
interface StoreService {
    /**
     * 상점 생성
     */
    fun createStore(request: StoreCreateDto): StoreDto
    
    /**
     * 상점 정보 수정
     */
    fun updateStore(id: Long, request: StoreUpdateDto): StoreDto

    /**
     * 상점 상태 변경
     */
    fun updateStoreStatus(id: Long, hasStock: Boolean, now: LocalDateTime): StoreDto

    /**
     * 점주가 직접 상점의 상태를 변경
     */
    fun updateStoreOnlyStatus(id: Long, newStatus: String): StoreDto

    /**
     * 상점 픽업 정보 변경
     */
    fun updateStorePickupInfo(id: Long, request: PickUpInfoDto): StoreDto

    /**
     * 상점 삭제 (Soft Delete)
     */
    fun deleteStore(id: Long): StoreDto

    /**
     * ID로 상점 조회
     */
    fun getStoreDetail(id: Long): StoreDto

    /**
     * 점주 ID로 상점 조회
     */
    fun getStoreByOwnerId(storeOwnerId: Long): List<StoreDto>

} 