package com.eatngo.redis.repository.search

import com.eatngo.TestApplication
import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.dto.SearchStoreMap
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

/**
 * 실제 Redis 서버와 통신하는 통합 테스트
 */
@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
@Transactional
@Tags("integration")
class SearchMapMapRedisRepositoryImplIntegrationTest(
    @Autowired @Qualifier("stringRedisTemplate") private val redisTemplate: RedisTemplate<String, String>,
    @Autowired private val objectMapper: ObjectMapper,
) : BehaviorSpec() {
    private val repository = SearchMapMapRedisRepositoryImpl(redisTemplate, objectMapper)

    init {
        this.given("SearchStoreMap 리스트와 좌표가 주어졌을 때") {
            val coordinate = CoordinateVO.from(latitude = 37.0, longitude = 127.0)
            val key = repository.getKey(coordinate)

            val storeMaps =
                listOf(
                    SearchStoreMap(storeID = 1L, storeName = "Store A", CoordinateVO.from(latitude = 37.000000, longitude = 127.000000)),
                    SearchStoreMap(storeID = 2L, storeName = "Store B", CoordinateVO.from(latitude = 37.000001, longitude = 127.000001)),
                )

            this.`when`("Redis에 저장하고 다시 조회하면") {
                repository.save(key, storeMaps)
                val result = repository.findByKey(key)

                this.then("정확히 같은 데이터가 조회된다") {
                    result shouldContainExactlyInAnyOrder storeMaps
                }
            }

            this.`when`("저장된 키를 삭제하면") {
                repository.save(key, storeMaps)
                repository.deleteByKey(key)
                val result = repository.findByKey(key)

                this.then("빈 리스트가 반환된다") {
                    result shouldBe emptyList()
                }
            }
        }
    }
}
