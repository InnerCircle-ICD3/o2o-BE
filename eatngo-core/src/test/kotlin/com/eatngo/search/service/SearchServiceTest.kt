package com.eatngo.search.service

import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStoreRepository
import com.eatngo.search.infra.SearchSuggestionRedisRepository
import com.eatngo.search.infra.SearchSuggestionRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.mockk

class SearchServiceTest :
    StringSpec({

        // mock 빈 생성
        val mockSearchStoreRepository = mockk<SearchStoreRepository>(relaxed = true)
        val mockSearchSuggestionRepository = mockk<SearchSuggestionRepository>(relaxed = true)
        val mockSearchMapRedisRepository = mockk<SearchMapRedisRepository>(relaxed = true)
        val mockSearchSuggestionRedisRepository = mockk<SearchSuggestionRedisRepository>(relaxed = true)

        val service =
            SearchService(
                searchStoreRepository = mockSearchStoreRepository,
                searchSuggestionRepository = mockSearchSuggestionRepository,
                searchMapRedisRepository = mockSearchMapRedisRepository,
                searchSuggestionRedisRepository = mockSearchSuggestionRedisRepository,
            )

        "getNineBoxesTopLeftFromCenter는 중심 좌표 기준 9개의 좌상단 좌표를 반환해야 한다" {
            val latitude = 37.0
            val longitude = 127.0
            val center = CoordinateVO.from(longitude = longitude, latitude = latitude)
            val result = service.getNineBoxesTopLeftFromCenter(center)

            /*
             * 1 2 3
             * 4 {center} 5
             * 6 7 8
             * */
            val expected =
                listOf(
                    // 6
                    CoordinateVO.from(longitude - service.cacheBoxSize, latitude - service.cacheBoxSize),
                    // 7
                    CoordinateVO.from(longitude, latitude - service.cacheBoxSize),
                    // 8
                    CoordinateVO.from(longitude + service.cacheBoxSize, latitude - service.cacheBoxSize),
                    // 4
                    CoordinateVO.from(longitude - service.cacheBoxSize, latitude),
                    // center
                    CoordinateVO.from(longitude, latitude),
                    // 5
                    CoordinateVO.from(longitude + service.cacheBoxSize, latitude),
                    // 1
                    CoordinateVO.from(longitude - service.cacheBoxSize, latitude + service.cacheBoxSize),
                    // 2
                    CoordinateVO.from(longitude, latitude + service.cacheBoxSize),
                    // 3
                    CoordinateVO.from(longitude + service.cacheBoxSize, latitude + service.cacheBoxSize),
                )

            result shouldContainExactly expected
        }
    })
