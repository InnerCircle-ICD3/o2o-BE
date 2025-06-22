package com.eatngo.search.repository

import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.dto.SearchStoreFoodTypeDto
import com.eatngo.search.dto.SearchStoreRdbDto
import java.time.LocalDateTime

interface SearchStoreQueryRepository {
    fun findByStoreId(storeId: Long): SearchStoreRdbDto

    fun findAddressByStoreId(storeId: Long): CoordinateVO

    fun findFoodTypesByStoreIds(storeIds: List<Long>): List<SearchStoreFoodTypeDto>

    fun findStoresByUpdateAt(pivotTime: LocalDateTime): List<SearchStoreRdbDto>
}
