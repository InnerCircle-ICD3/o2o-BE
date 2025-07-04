package com.eatngo.store.usecase

import com.eatngo.review.service.StoreReviewStatsService
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.service.StoreService
import com.eatngo.subscription.service.StoreSubscriptionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreQueryUseCase(
    private val storeService: StoreService,
    private val storeReviewStatsService: StoreReviewStatsService,
    private val storeSubscriptionService: StoreSubscriptionService
) {
    @Transactional(readOnly = true)
    fun getStoreById(storeId: Long): StoreDto {
        val store = storeService.getStoreById(storeId)
        val reviewStats = storeReviewStatsService.getStoreReviewStats(storeId)
        return StoreDto.from(store, reviewStats)
    }

    @Transactional(readOnly = true)
    fun getStoreByIdWithSubscription(storeId: Long, customerId: Long?): Pair<StoreDto, Boolean> {
        val store = storeService.getStoreById(storeId)
        val reviewStats = storeReviewStatsService.getStoreReviewStats(storeId)
        val storeDto = StoreDto.from(store, reviewStats)
        val isFavorite = storeSubscriptionService.isSubscribed(storeId, customerId)
        return storeDto to isFavorite
    }

    @Transactional(readOnly = true)
    fun getStoresByStoreOwnerId(storeOwnerId: Long): List<StoreDto> {
        val stores = storeService.getStoresByStoreOwnerId(storeOwnerId)
        val storeIds = stores.map { it.id }
        val reviewStatsMap = storeReviewStatsService.getStoreReviewStats(storeIds)
        
        return stores.map { store ->
            val reviewStats = reviewStatsMap[store.id]
            StoreDto.from(store, reviewStats)
        }
    }
}