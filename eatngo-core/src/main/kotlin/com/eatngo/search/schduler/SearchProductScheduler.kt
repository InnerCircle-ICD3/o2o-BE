package com.eatngo.search.schduler

import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.Box
import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStoreRepository
import com.eatngo.search.service.SearchService
import com.eatngo.store.domain.Store
import com.eatngo.store.infra.StorePersistence
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
    private val storePersistence: StorePersistence,
    private val searchStoreRepository: SearchStoreRepository,
    private val searchMapRedisRepository: SearchMapRedisRepository,
    private val searchService: SearchService,
) {
    /**
     * 상품 검색 인덱스를 업데이트합니다.
     * 현재는 10분마다 실행되도록 설정되어 있습니다.
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    private fun updateSearchIndexFromStore() {
        val pivotTime = LocalDateTime.now().minusMinutes(10)
        val stores: List<Store> = storePersistence.findByUpdatedAt(pivotTime = pivotTime)
        if (stores.isEmpty()) {
            return
        }

        val deleteStoreIds = mutableListOf<Long>()
        val updateStores = mutableListOf<SearchStore>()
        val redisBoxMap = mutableMapOf<String, Box>()

        for (store in stores) {
            // MongoDB 업데이트 정보
            if (store.deletedAt != null) {
                deleteStoreIds.add(store.id)
            } else {
                updateStores.add(SearchStore.from(store))
            }
            // Redis 업데이트 정보
            val box =
                searchService.getBox(
                    latitude = store.address.coordinate.latitude,
                    longitude = store.address.coordinate.longitude,
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
