package com.eatngo.search.infra

import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreFoodTypes
import java.time.LocalDateTime

interface SearchStorePersistence {
    fun syncStore(storeId: Long): SearchStore

    fun findFoodTypesByProductUpdatedAt(pivotTime: LocalDateTime): List<SearchStoreFoodTypes>
}
