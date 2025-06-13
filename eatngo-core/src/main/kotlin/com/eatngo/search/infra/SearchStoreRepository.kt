package com.eatngo.search.infra

import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreFoodTypes
import com.eatngo.search.dto.AutoCompleteStoreNameDto
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.SearchFilter

interface SearchStoreRepository {
    fun findBox(box: Box): List<SearchStore>

    fun searchStore(
        longitude: Double,
        latitude: Double,
        maxDistance: Double,
        searchFilter: SearchFilter,
        size: Int,
    ): List<SearchStore>

    // deprecated: use searchStore instead
    fun autocompleteStoreName(
        keyword: String,
        size: Int = 5,
    ): List<AutoCompleteStoreNameDto>

    fun save(searchStore: SearchStore)

    fun saveAll(searchStoreList: List<SearchStore>)

    fun updateStoreStatus(
        storeId: Long,
        status: String,
    )

    fun updateFoodTypesAll(searchStoreList: List<SearchStoreFoodTypes>)

    fun deleteIds(deleteIds: List<Long>)

    fun deleteId(deleteId: Long)
}
