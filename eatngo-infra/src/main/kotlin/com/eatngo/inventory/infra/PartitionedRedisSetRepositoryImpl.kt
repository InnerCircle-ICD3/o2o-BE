package com.eatngo.inventory.infra

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository

/**
 * 파티셔닝된 Redis Set 관리 구현체
 *
 * productId를 파티션 개수로 나눈 나머지를 사용하여 분배합니다.
 */
@Repository
class PartitionedRedisSetRepositoryImpl(
    @Qualifier("stringRedisTemplate")
    private val stringRedisTemplate: RedisTemplate<String, String>
) : PartitionedRedisSetRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        // Lua 스크립트: 특정 파티션의 모든 멤버를 가져오고 해당 파티션을 삭제
        private const val LUA_POP_AND_DELETE_PARTITION = """
            local members = redis.call('SMEMBERS', KEYS[1])
            redis.call('DEL', KEYS[1])
            return members
        """
    }

    override fun add(baseKey: String, productId: Long, partitionCount: Int) {
        val partitionKey = getPartitionKey(baseKey, productId, partitionCount)
        stringRedisTemplate.opsForSet().add(partitionKey, productId.toString())
    }

    override fun members(baseKey: String, partitionCount: Int): Set<String> {
        val allMembers = mutableSetOf<String>()

        for (i in 0 until partitionCount) {
            val partitionKey = "$baseKey:$i"
            val members = stringRedisTemplate.opsForSet().members(partitionKey)
            if (!members.isNullOrEmpty()) {
                allMembers.addAll(members)
            }
        }

        return allMembers
    }

    override fun remove(baseKey: String, productId: Long, partitionCount: Int) {
        val partitionKey = getPartitionKey(baseKey, productId, partitionCount)
        stringRedisTemplate.opsForSet().remove(partitionKey, productId.toString())
    }

    override fun deleteAll(baseKey: String, partitionCount: Int) {
        val keys = (0 until partitionCount).map { "$baseKey:$it" }
        if (keys.isNotEmpty()) {
            stringRedisTemplate.delete(keys)
        }
    }

    override fun popAndDeletePartition(baseKey: String, partitionIndex: Int): Set<String> {
        val partitionKey = "$baseKey:$partitionIndex"
        val redisScript = DefaultRedisScript<Set<String>>().apply {
            setScriptText(LUA_POP_AND_DELETE_PARTITION.trimIndent())
            resultType = Set::class.java as Class<Set<String>>
        }

        return stringRedisTemplate.execute(redisScript, listOf(partitionKey)) ?: emptySet()
    }

    /**
     * productId를 기반으로 파티션 키를 생성합니다.
     */
    private fun getPartitionKey(baseKey: String, productId: Long, partitionCount: Int): String {
        val partitionIndex = (productId % partitionCount).toInt()
        return "$baseKey:$partitionIndex"
    }
}
