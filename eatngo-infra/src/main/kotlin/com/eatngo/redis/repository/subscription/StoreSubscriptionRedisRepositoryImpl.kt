package com.eatngo.redis.repository.subscription

import com.eatngo.redis.utils.writeValueToJson
import com.eatngo.subscription.infra.StoreSubscriptionRedisRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

/**
 * 구독 정보 Redis 저장소
 */
@Repository
class StoreSubscriptionRedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
): StoreSubscriptionRedisRepository {
    companion object {
        private const val STORE_SUBSCRIPTION_COUNT_KEY = "store:%d:subscription:count"
        private const val CUSTOMER_SUBSCRIPTION_LIST_KEY = "customer:%d:subscriptions"
        private const val STORE_SUBSCRIPTION_LIST_KEY = "store:%d:subscribers"
        private const val CACHE_TTL_SECONDS = 60 * 30 // 30분
    }

    /**
     * 매장 구독자 수 캐싱
     */
    override fun setStoreSubscriptionCount(storeId: Long, count: Int) {
        val key = STORE_SUBSCRIPTION_COUNT_KEY.format(storeId)
        redisTemplate.opsForValue().set(key, count.toString())
        redisTemplate.expire(key, CACHE_TTL_SECONDS.toLong(), TimeUnit.SECONDS)
    }

    /**
     * 매장 구독자 수 조회
     */
    override fun getStoreSubscriptionCount(storeId: Long): Int? {
        val key = STORE_SUBSCRIPTION_COUNT_KEY.format(storeId)
        val value = redisTemplate.opsForValue().get(key)
        return value?.toIntOrNull()
    }

    /**
     * 매장 구독자 수 증가
     */
    override fun incrementStoreSubscriptionCount(storeId: Long): Long {
        val key = STORE_SUBSCRIPTION_COUNT_KEY.format(storeId)
        return redisTemplate.opsForValue().increment(key, 1) ?: 1L
    }

    /**
     * 매장 구독자 수 감소
     */
    override fun decrementStoreSubscriptionCount(storeId: Long): Long {
        val key = STORE_SUBSCRIPTION_COUNT_KEY.format(storeId)
        return redisTemplate.opsForValue().increment(key, -1) ?: 0L
    }


    /**
     * 고객 구독 목록 캐싱
     */
    override fun setCustomerSubscriptionList(customerId: Long, storeIds: List<Long>) {
        val key = CUSTOMER_SUBSCRIPTION_LIST_KEY.format(customerId)
        val value = objectMapper.writeValueAsString(storeIds)
        redisTemplate.opsForValue().set(key, value)
        redisTemplate.expire(key, CACHE_TTL_SECONDS.toLong(), TimeUnit.SECONDS)
    }

    /**
     * 고객 구독 목록 조회
     */
    override fun getCustomerSubscriptionList(customerId: Long): List<Long>? {
        val key = CUSTOMER_SUBSCRIPTION_LIST_KEY.format(customerId)
        val value = redisTemplate.opsForValue().get(key)
        return value?.let {
            try {
                objectMapper.readValue(it, Array<Long>::class.java).toList()
            } catch (e: Exception) {
                null //TODO: logging 처리
            }
        }
    }

    /**
     * 고객 구독 목록에 매장 추가
     */
    override fun addStoreToCustomerSubscriptions(customerId: Long, storeId: Long) {
        val storeIds = getCustomerSubscriptionList(customerId)?.toMutableList() ?: mutableListOf()
        if (!storeIds.contains(storeId)) {
            storeIds.add(storeId)
            setCustomerSubscriptionList(customerId, storeIds)
        }
    }

    /**
     * 고객 구독 목록에서 매장 제거
     */
    override fun removeStoreFromCustomerSubscriptions(customerId: Long, storeId: Long) {
        val storeIds = getCustomerSubscriptionList(customerId)?.toMutableList() ?: return
        if (storeIds.contains(storeId)) {
            storeIds.remove(storeId)
            setCustomerSubscriptionList(customerId, storeIds)
        }
    }

    /**
     * 캐시 무효화
     */
    override fun invalidateCache(storeId: Long, customerId: Long) {
        val storeCountKey = STORE_SUBSCRIPTION_COUNT_KEY.format(storeId)
        val customerListKey = CUSTOMER_SUBSCRIPTION_LIST_KEY.format(customerId)
        val storeListKey = STORE_SUBSCRIPTION_LIST_KEY.format(storeId)

        redisTemplate.delete(storeCountKey)
        redisTemplate.delete(customerListKey)
        redisTemplate.delete(storeListKey)
    }
}