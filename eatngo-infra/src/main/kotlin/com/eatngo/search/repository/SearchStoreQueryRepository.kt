package com.eatngo.search.repository

import com.eatngo.search.dto.SearchStoreFoodTypeDto
import com.eatngo.search.dto.SearchStoreRdbDto
import java.time.LocalDateTime

interface SearchStoreQueryRepository {
    fun findByStoreId(storeId: Long): SearchStoreRdbDto

    fun findFoodTypesByProductUpdatedAt(pivotTime: LocalDateTime): List<SearchStoreFoodTypeDto>

    fun findAllByUpdatedAtAfter(pivotTime: LocalDateTime): List<SearchStoreRdbDto>
}
