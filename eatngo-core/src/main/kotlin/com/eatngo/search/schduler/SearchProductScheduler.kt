package com.eatngo.search.schduler

import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.Box
import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStorePersistence
import com.eatngo.search.infra.SearchStoreRepository
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
    private val searchMapRedisRepository: SearchMapRedisRepository,
    private val searchStorePersistence: SearchStorePersistence,
    private val searchService: SearchService,
) {
    companion object {
        private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)
    }

    /**
     * 상품 검색 인덱스 업데이트를 위한 스케줄러
     * 매 10분마다 실행되어, 최근 10분 이내에 업데이트된 상품의 foodTypes를 업데이트합니다.
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    private fun updateSearchStoreFoodTypes() {
        try {
            val pivotTime = LocalDateTime.now().minusMinutes(11)
            val foodTypesList = searchStorePersistence.findFoodTypesByProductUpdatedAt(pivotTime)
            if (foodTypesList.isEmpty()) {
                return
            }
            // 검색 인덱스 업데이트
            searchStoreRepository.updateFoodTypesAll(foodTypesList)
        } catch (e: Exception) {
            // 예외 발생 시 로깅
            log.error("Error updating search index from store", e)
        }
    }

    /**
     * todo: 로직 점검 및 사용 여부 결정
     * 상품 검색 인덱스를 업데이트합니다.
     */
    private fun updateSearchIndexFromStore() {
        val pivotTime = LocalDateTime.now().minusMinutes(61)
        val stores: List<SearchStore> = searchStorePersistence.syncAllStoresByUpdateAt(pivotTime = pivotTime)
        if (stores.isEmpty()) {
            return
        }

        val deleteStoreIds = mutableListOf<Long>()
        val updateStores = mutableListOf<SearchStore>()
        val redisBoxMap = mutableMapOf<String, Box>()

        for (store in stores) {
            // MongoDB 업데이트 정보
            if (store.deletedAt != null) {
                deleteStoreIds.add(store.storeId)
            } else {
                updateStores.add(store)
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

        // 검색 인덱스 업데이트
        searchStoreRepository.saveAll(updateStores)
        searchStoreRepository.deleteIds(deleteStoreIds)

        // 매장 위치 정보 업데이트
        for ((_, box) in redisBoxMap) {
            searchService.saveBoxRedis(box = box)
        }
    }
}
