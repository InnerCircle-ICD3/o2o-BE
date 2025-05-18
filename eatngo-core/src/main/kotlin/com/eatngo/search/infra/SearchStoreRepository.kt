package com.eatngo.search.infra

import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.SearchFilter

interface SearchStoreRepository {
    fun findByLocation(
        lng: Double,
        lat: Double,
        maxDistance: Double
    ): List<SearchStore>

    fun searchStore(
        lng: Double,
        lat: Double,
        maxDistance: Double,
        searchFilter: SearchFilter? = null,
        page: Int = 0,
        size: Int = 20,
    ): List<SearchStore>
}