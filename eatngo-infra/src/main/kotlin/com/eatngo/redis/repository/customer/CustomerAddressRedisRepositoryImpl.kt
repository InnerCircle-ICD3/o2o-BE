package com.eatngo.redis.repository.customer

import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.infra.CustomerAddressRedisRepository
import com.eatngo.redis.utils.writeValueToJson
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class CustomerAddressRedisRepositoryImpl(
    private val stringRedisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) : CustomerAddressRedisRepository {
    override fun getKey(customerId: Long): String = "customer:$customerId:address"

    override fun findCustomerAddress(key: String): List<CustomerAddress> {
        return stringRedisTemplate.opsForList().range(key, 0, -1)
            ?.map { address -> objectMapper.readValue(address, CustomerAddress::class.java) }
            ?: emptyList()
    }

    override fun save(
        key: String,
        address: CustomerAddress,
    ) {
        stringRedisTemplate.opsForList().rightPush(key, objectMapper.writeValueToJson(address))
    }

    override fun deleteValue(
        key: String,
    ) {
        stringRedisTemplate.unlink(key)
    }
}
