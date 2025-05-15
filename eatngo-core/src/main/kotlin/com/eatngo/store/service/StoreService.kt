package com.eatngo.store.service

import com.eatngo.store.dto.*
import java.time.ZonedDateTime

/**
 * 상점 서비스 인터페이스
 */
interface StoreService {
    /**
     * 상점 생성
     */
    suspend fun createStore(request: StoreDto.CreateRequest): StoreDto.CreateResponse
    
    /**
     * 상점 수정
     */
    suspend fun updateStore(id: Long, request: StoreDto.UpdateRequest): StoreDto.Response
    
    /**
     * 상점 상태 변경
     */
    suspend fun updateStoreStatus(id: Long, request: StoreDto.StatusUpdateRequest): StoreDto.Response
    
    /**
     * 상점 픽업 정보 변경
     */
    suspend fun updateStorePickupInfo(id: Long, request: StoreDto.PickupInfoUpdateRequest): StoreDto.Response
    
    /**
     * ID로 상점 조회
     */
    suspend fun getStoreById(id: Long): StoreDto.Response
    
    /**
     * 이름으로 상점 검색
     */
    suspend fun searchStoresByName(name: String, limit: Int, offset: Int): List<StoreDto.SummaryResponse>
    
    /**
     * 주소 기반 주변 상점 검색
     */
    suspend fun findNearbyStores(location: LocationDto, radius: Double, limit: Int, offset: Int): List<StoreDto.SummaryResponse>
    
    /**
     * 카테고리별 상점 검색
     */
    suspend fun findStoresByCategory(category: String, limit: Int, offset: Int): List<StoreDto.SummaryResponse>
    
    /**
     * 현재 영업 중인 상점 조회
     */
    suspend fun findOpenStores(limit: Int, offset: Int): List<StoreDto.SummaryResponse>
    
    /**
     * 현재 픽업 가능한 상점 조회
     */
    suspend fun findAvailableForPickupStores(limit: Int, offset: Int): List<StoreDto.SummaryResponse>
    
    /**
     * 내일 픽업 가능한 상점 조회
     */
    suspend fun findAvailableForTomorrowStores(limit: Int, offset: Int): List<StoreDto.SummaryResponse>
    
    /**
     * 특정 날짜에 픽업 가능한 상점 조회
     */
    suspend fun findAvailableForDateStores(targetDate: ZonedDateTime, limit: Int, offset: Int): List<StoreDto.SummaryResponse>
    
    /**
     * 통합 상점 검색 (키워드, 위치, 카테고리, 영업여부 등 복합 조건)
     */
    suspend fun searchStores(request: StoreDto.SearchRequest): List<StoreDto.SummaryResponse>
    
    /**
     * 점주 ID로 상점 조회
     */
    suspend fun getStoreByOwnerId(ownerId: String): StoreDto.Response?
    
    /**
     * 상점 삭제 (Soft Delete)
     */
    suspend fun deleteStore(id: Long): Boolean
} 