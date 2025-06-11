package com.eatngo.search.infra

import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.domain.SearchStore

interface SearchMapRedisRepository {
    fun getKey(topLeft: CoordinateVO): String

    fun save(
        key: String,
        value: List<SearchStore>,
    )

    fun saveStore(
        key: String,
        store: SearchStore,
    )

    fun findByKey(key: String): List<SearchStore>

    fun deleteByKey(key: String)

    fun deleteOneByKey(
        key: String,
        storeId: Long,
    )
}
