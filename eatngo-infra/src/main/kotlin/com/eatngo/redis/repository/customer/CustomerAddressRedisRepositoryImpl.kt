package com.eatngo.redis.repository.customer

import com.eatngo.customer.dto.CustomerAddressDto
import com.eatngo.customer.infra.CustomerAddressRedisRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class CustomerAddressRedisRepositoryImpl(
    private val stringRedisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) : CustomerAddressRedisRepository {
    override fun getKey(customerId: Long): String = "customer:$customerId:address"

    override fun findCustomerAddress(key: String) =
        stringRedisTemplate.opsForSet()
            .members(key)
            ?.map { objectMapper.readValue(it, CustomerAddressDto::class.java) }
            ?.toMutableSet()
            ?: mutableSetOf()

    override fun save(
        key: String,
        addresses: MutableSet<CustomerAddressDto>,
    ) {
        addresses.forEach {
            val json = objectMapper.writeValueAsString(it)
            stringRedisTemplate.opsForSet().add(key, json)
        }
    }

    override fun deleteValue(
        key: String,
    ) {
        stringRedisTemplate.unlink(key)
    }
}
