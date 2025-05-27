package com.eatngo.search.infra

import com.eatngo.common.type.Coordinate
import com.eatngo.search.dto.SearchStoreMap

interface SearchMapRedisRepository {
    fun getKey(topLeft: Coordinate): String

    fun save(
        key: String,
        value: List<SearchStoreMap>,
    )

    fun findByKey(key: String): List<SearchStoreMap>

    fun deleteByKey(key: String)
}
