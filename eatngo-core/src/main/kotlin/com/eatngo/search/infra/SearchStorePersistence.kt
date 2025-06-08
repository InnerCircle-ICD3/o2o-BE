package com.eatngo.search.infra

import com.eatngo.search.domain.SearchStoreFoodTypes
import java.time.LocalDateTime

interface SearchStorePersistence {
    fun findFoodTypesByProductUpdatedAt(pivotTime: LocalDateTime): List<SearchStoreFoodTypes>
}
