package com.eatngo.search.infra

import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.SearchFilter

interface SearchStoreRepository {
    fun findBox(box: Box): List<SearchStore>

    fun searchStore(
        longitude: Double,
        latitude: Double,
        maxDistance: Double,
        searchFilter: SearchFilter? = null,
        page: Int = 0,
        size: Int = 20,
    ): List<SearchStore>

    fun searchStoreRecommend(
        keyword: String,
        size: Int = 10,
    ): List<String>
}
