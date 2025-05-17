package com.eatngo.store.service

import com.eatngo.store.dto.*

/**
 * 상점 서비스 인터페이스
 */
interface StoreService {
    /**
     * 상점 생성
     */
    suspend fun createStore(request: StoreCreateDto): StoreDto
    
    /**
     * 상점 정보 수정
     */
    suspend fun updateStore(id: Long, request: StoreUpdateDto): StoreDto

    /**
     * 상점 상태 변경
     */
    suspend fun updateStoreStatus(id: Long, request: StatusUpdateRequest): StoreDto
    
    /**
     * 상점 픽업 정보 변경
     */
    suspend fun updateStorePickupInfo(id: Long, request: PickupInfoUpdateRequest): StoreDto

    /**
     * 상점 삭제 (Soft Delete)
     */
    suspend fun deleteStore(id: Long): Boolean

    /**
     * ID로 상점 조회
     */
    suspend fun getStoreById(id: Long): StoreDto

    /**
     * 통합 상점 검색 (키워드, 위치, 카테고리, 영업여부 등 복합 조건)
     */
    suspend fun searchStores(request: StoreSearchDto): List<StoreSummary>
    
    /**
     * 점주 ID로 상점 조회
     */
    suspend fun getStoreByOwnerId(ownerId: String): StoreDto?
    
    /**
     * 스토어 상세 정보 조회 및 CustomerStoreDetailResponse 생성
     */
    suspend fun getStoreDetailById(storeId: Long): CustomerStoreDetailResponse
    
    /**
     * 스토어 검색 및 CustomerStoreListResponse 생성
     */
    suspend fun searchStoresWithResponse(request: StoreSearchDto): CustomerStoreListResponse
} 