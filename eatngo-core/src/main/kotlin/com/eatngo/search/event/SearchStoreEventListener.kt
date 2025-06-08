package com.eatngo.search.event

import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStorePersistence
import com.eatngo.search.infra.SearchStoreRepository
import com.eatngo.search.service.SearchService
import com.eatngo.store.event.StoreCUDEvent
import com.eatngo.store.event.StoreCUDEventType
import com.eatngo.store.infra.StorePersistence
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class SearchStoreEventListener(
    private val storePersistence: StorePersistence,
    private val searchStoreRepository: SearchStoreRepository,
    private val searchMapRedisRepository: SearchMapRedisRepository,
    private val searchStorePersistence: SearchStorePersistence,
    private val searchService: SearchService,
) {
    /**
     * 매장 CUD(Create/Update/Delete) 이벤트 처리
     * 검색 시스템에서 매장 정보 변경을 반영하기 위한 메소드
     */
    @Async
    @EventListener
    fun handleStoreCUDEvent(event: StoreCUDEvent) {
        when (event.eventType) {
            StoreCUDEventType.CREATED,
            StoreCUDEventType.UPDATED,
            -> {
                // TODO PostgreSQL에서 조인된 Store + Product 정보 읽기 → Mongo 저장
//                storePersistence.syncStore(event.storeId)
            }
            StoreCUDEventType.DELETED -> {
                searchStoreRepository.deleteId(event.storeId)
            }
        }
    }

    fun handleStoreStatusChangedEvent() {
    }
}
