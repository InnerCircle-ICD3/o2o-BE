package com.eatngo.search.schduler

import com.eatngo.review.service.StoreReviewStatsService
import com.eatngo.search.constant.SuggestionType
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreStatus
import com.eatngo.search.domain.SearchSuggestion
import com.eatngo.search.dto.Box
import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStorePersistence
import com.eatngo.search.infra.SearchStoreRepository
import com.eatngo.search.infra.SearchSuggestionRepository
import com.eatngo.search.service.SearchService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 상품 검색 인덱스 업데이트를 위한 스케줄러
 * 화면에 보여지지 않지만 검색되는 필드(foodType 등)를 주기적으로 업데이트 합니다
 * 우선은 Scheduled로 구현하였으나, 추후에 필요시 Spring Batch로 변경할 수 있습니다.
 */
@Component
@ConditionalOnProperty(
    name = ["eatngo.search.scheduler.enabled"],
    havingValue = "true", // 우선 현재 store-owner-api 에서 스케줄러 돌도록 설정
    matchIfMissing = false,
)
class SearchProductScheduler(
    private val searchStoreRepository: SearchStoreRepository,
    private val searchSuggestionRepository: SearchSuggestionRepository,
    private val searchMapRedisRepository: SearchMapRedisRepository,
    private val searchStorePersistence: SearchStorePersistence,
    private val storeReviewStatsService: StoreReviewStatsService,
    private val searchService: SearchService,
) {
    companion object {
        private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)
    }

    /**
     * 상품 검색 인덱스를 업데이트합니다.
     * 하루에 한 번씩 실행되어, 최근 이내에 업데이트된 매장 정보를 기반으로 검색 인덱스를 업데이트합니다.
     * @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * ?")
    private fun updateSearchIndex() {
        try {
            val pivotTime = LocalDateTime.now().minusMinutes(60 * 24) // 하루 전 시간
            val stores: List<SearchStore> = searchStorePersistence.findStoresByUpdateAt(pivotTime = pivotTime)
            if (stores.isEmpty()) {
                return
            }

            val deleteStoreIds = mutableListOf<Long>()
            val updateStores = mutableListOf<SearchStore>()
            val updateSuggestion = mutableListOf<SearchSuggestion>()
            val foodTypes = mutableSetOf<String>()
            val redisBoxMap = mutableMapOf<String, Box>()

            // storeId -> foodTypes 리스트로 매핑
            val foodTypeMap =
                searchStorePersistence
                    .findFoodTypesByStoreIds(stores.map { it.storeId })
                    .groupBy({ it.storeId }, { it.foodTypes }) // List<List<String>>
                    .mapValues { (_, lists) -> lists.flatten() } // 하나의 List<String>으로 병합

            // storeId 기준으로 빠르게 매핑
            val storeMap = stores.associateBy { it.storeId }
            // 리뷰 통계 데이터 획득
            val reviewStatsMap =
                storeReviewStatsService.getStoreReviewStats(
                    storeIds = storeMap.keys.toList(),
                ) // 리뷰 통계 정보 추가
            // 업데이트 정보 생성
            for ((storeId, store) in storeMap) {
                foodTypeMap[storeId]?.let { types ->
                    store.foodTypes = types
                    foodTypes.addAll(types)
                }

                if (store.deletedAt != null || store.status == SearchStoreStatus.PENDING) {
                    deleteStoreIds.add(storeId)
                } else {
                    reviewStatsMap[storeId]?.let { reviewStats ->
                        store.totalReviewCount = reviewStats.totalReviewCount
                        store.averageRating = reviewStats.averageRating
                    }
                    updateStores.add(store)
                    // 검색어 정보 업데이트
                    updateSuggestion.add(
                        SearchSuggestion.from(
                            keyword = store.storeName,
                            type = SuggestionType.STORE_NAME,
                            keywordId = storeId,
                        ),
                    )
                }
                // Redis 업데이트 정보
                val box =
                    searchService.getBox(
                        latitude = store.coordinate.latitude,
                        longitude = store.coordinate.longitude,
                    )
                val redisKey = searchMapRedisRepository.getKey(box.topLeft)
                redisBoxMap[redisKey] = box
            }
            foodTypes.forEach { foodType ->
                updateSuggestion.add(
                    SearchSuggestion.from(
                        keyword = foodType,
                        type = SuggestionType.FOOD_TYPE,
                    ),
                )
            }

            // 검색 인덱스 업데이트
            searchStoreRepository.saveAll(updateStores)
            searchStoreRepository.deleteIds(deleteStoreIds)

            // 매장 위치 정보 업데이트
            for ((_, box) in redisBoxMap) {
                searchService.saveBoxRedis(box = box)
            }

            // 키워드 정보 업데이트
            searchSuggestionRepository.saveSuggestionList(updateSuggestion)
            searchSuggestionRepository.deleteByKeywordIdList(deleteStoreIds)
        } catch (e: Exception) {
            log.error("Error updating search index from store", e)
        }
    }
}
