package com.eatngo.search.event

import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStorePersistence
import com.eatngo.search.infra.SearchStoreRepository
import com.eatngo.search.service.SearchService
import com.eatngo.store.event.StoreCUDEvent
import com.eatngo.store.event.StoreCUDEventType
import com.eatngo.store.event.StoreStatusChangedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class SearchStoreEventListener(
    private val searchStoreRepository: SearchStoreRepository,
    private val searchMapRedisRepository: SearchMapRedisRepository,
    private val searchStorePersistence: SearchStorePersistence,
    private val searchService: SearchService,
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * 매장 CUD(Create/Update/Delete) 이벤트 처리
     * 검색 시스템에서 매장 정보 변경을 반영하기 위한 메소드
     */
    @Async
    @EventListener
    fun handleStoreCUDEvent(event: StoreCUDEvent) {
        val rdbStore = searchStorePersistence.syncStore(event.storeId)
        val box =
            searchService.getBox(
                latitude = rdbStore.coordinate.latitude,
                longitude = rdbStore.coordinate.longitude,
            )
        val searchMapKey = searchMapRedisRepository.getKey(box.topLeft)
        try {
            when (event.eventType) {
                StoreCUDEventType.CREATED,
                StoreCUDEventType.UPDATED,
                -> {
                    searchStoreRepository.save(rdbStore)
                    // 매장 정보 변경 후, 검색 맵 캐시를 갱신
                    searchMapRedisRepository.saveStore(
                        key = searchMapKey,
                        store = rdbStore,
                    )
                }
                StoreCUDEventType.DELETED -> {
                    searchStoreRepository.deleteId(event.storeId)
                    // 매장 삭제 후, 검색 맵 캐시에서 해당 매장 정보 제거
                    searchMapRedisRepository.deleteOneByKey(
                        key = searchMapKey,
                        storeId = event.storeId,
                    )
                }
            }
        } catch (e: Exception) {
            log.error("Failed to handle StoreCUDEvent for storeId: ${event.storeId}", e)
            return
        }
    }

    /**
     * 매장 상태 변경 이벤트 처리
     */
    @Async
    @EventListener
    fun handleStoreStatusChangedEvent(event: StoreStatusChangedEvent) {
        try {
            searchStoreRepository.updateStoreStatus(
                storeId = event.storeId,
                status = event.currentStatus.name,
            )
        } catch (e: Exception) {
            log.error(
                "Failed to update store status in search repository for storeId: ${event.storeId}, status: ${event.currentStatus}",
                e,
            )
        }
    }
}
