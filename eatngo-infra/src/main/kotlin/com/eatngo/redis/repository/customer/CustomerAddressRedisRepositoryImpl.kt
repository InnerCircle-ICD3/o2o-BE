package com.eatngo.redis.repository.customer

import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.infra.CustomerAddressRedisRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class CustomerAddressRedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>,
) : CustomerAddressRedisRepository {
    override fun getKey(customerId: Long): String = "customer:$customerId:address"

    override fun findCustomerAddress(key: String): List<CustomerAddress> {
        TODO("Not yet implemented")
    }

    override fun save(
        key: String,
        address: CustomerAddress,
    ): String {
        TODO("Not yet implemented")
    }

    override fun deleteAddressId(
        key: String,
        addressId: Long,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
