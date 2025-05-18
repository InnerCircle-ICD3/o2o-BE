package com.eatngo.search.infra

import com.eatngo.search.domain.SearchStore

interface SearchStoreRepository {
    fun findByLocation(
        lng: Double,
        lat: Double,
        maxDistance: Double
    ): List<SearchStore>
}