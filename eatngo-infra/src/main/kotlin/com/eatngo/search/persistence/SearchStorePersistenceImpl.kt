package com.eatngo.search.persistence

import com.eatngo.search.domain.SearchStoreFoodTypes
import com.eatngo.search.infra.SearchStorePersistence
import com.eatngo.search.repository.JpaSearchStoreRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SearchStorePersistenceImpl(
    private val jpaSearchStoreRepository: JpaSearchStoreRepository,
) : SearchStorePersistence {
    override fun findFoodTypesByProductUpdatedAt(pivotTime: LocalDateTime): List<SearchStoreFoodTypes> {
        val foodTypeDto = jpaSearchStoreRepository.findFoodTypesByProductUpdatedAt(pivotTime)
        return foodTypeDto.map { SearchStoreFoodTypes.from(it.storeId, it.foodTypes) }
    }
}
