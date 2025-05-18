package com.eatngo.search.infra

interface SearchMapRedisRepository {
    fun save(key: String, value: String)
    fun findByKey(key: String): String?
    fun deleteByKey(key: String)
}