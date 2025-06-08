package com.eatngo.search.infra

import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreFoodTypes
import com.eatngo.search.dto.AutoCompleteStoreNameDto
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.SearchFilter
import com.eatngo.search.dto.SearchStoreWithDistance

interface SearchStoreRepository {
    fun findBox(box: Box): List<SearchStore>

    fun listStore(
        longitude: Double,
        latitude: Double,
        maxDistance: Double,
        searchFilter: SearchFilter,
        page: Int = 0,
        size: Int = 20,
    ): List<SearchStoreWithDistance>

    fun searchStore(
        longitude: Double,
        latitude: Double,
        maxDistance: Double,
        searchText: String,
        page: Int = 0,
        size: Int = 20,
    ): List<SearchStore>

    fun autocompleteStoreName(
        keyword: String,
        size: Int = 5,
    ): List<AutoCompleteStoreNameDto>

    fun save(searchStore: SearchStore)

    fun saveAll(searchStoreList: List<SearchStore>)

    fun updateFoodTypesAll(searchStoreList: List<SearchStoreFoodTypes>)

    fun deleteIds(deleteIds: List<Long>)

    fun deleteId(deleteId: Long)
}
