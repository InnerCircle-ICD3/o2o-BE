package com.eatngo.search.persistence

import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreFoodTypes
import com.eatngo.search.infra.SearchStorePersistence
import com.eatngo.search.repository.SearchStoreQueryRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SearchStorePersistenceImpl(
    private val searchStoreRepository: SearchStoreQueryRepository,
) : SearchStorePersistence {
    override fun syncStore(storeId: Long): SearchStore {
        val storeDto =
            searchStoreRepository
                .findByStoreId(storeId)
        return storeDto.toSearchStore()
    }

    override fun findFoodTypesByStoreIds(storeIds: List<Long>): List<SearchStoreFoodTypes> {
        val foodTypeDto = searchStoreRepository.findFoodTypesByStoreIds(storeIds)
        return foodTypeDto.map {
            SearchStoreFoodTypes.from(
                it.storeId,
                it.foodTypes.split(",").map { it.trim() },
            )
        }
    }

    override fun findStoresByUpdateAt(pivotTime: LocalDateTime): List<SearchStore> {
        val stores = searchStoreRepository.findStoresByUpdateAt(pivotTime)
        return stores.map { it.toSearchStore() }
    }

    override fun syncAllStoresByUpdateAt(pivotTime: LocalDateTime): List<SearchStore> {
        val stores = searchStoreRepository.findAllByUpdatedAtAfter(pivotTime)
        return stores.map { it.toSearchStore() }
    }
}
