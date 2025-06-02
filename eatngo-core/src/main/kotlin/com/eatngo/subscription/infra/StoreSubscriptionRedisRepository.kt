package com.eatngo.subscription.infra

interface StoreSubscriptionRedisRepository {
    fun setStoreSubscriptionCount(storeId: Long, count: Int)
    fun getStoreSubscriptionCount(storeId: Long): Int?
    fun incrementStoreSubscriptionCount(storeId: Long): Long
    fun decrementStoreSubscriptionCount(storeId: Long): Long

    fun setCustomerSubscriptionList(customerId: Long, storeIds: List<Long>)
    fun getCustomerSubscriptionList(customerId: Long): List<Long>?
    fun addStoreToCustomerSubscriptions(customerId: Long, storeId: Long)
    fun removeStoreFromCustomerSubscriptions(customerId: Long, storeId: Long)

    fun invalidateCache(storeId: Long, customerId: Long)
}
