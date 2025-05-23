package com.eatngo.mongo.repository.search

import com.eatngo.mongo.entity.search.SearchStoreEntity
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.SearchFilter
import com.eatngo.search.dto.StoreStatus
import com.eatngo.search.infra.SearchStoreRepository
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.data.geo.Box
import org.springframework.data.geo.Shape
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import com.eatngo.search.dto.Box as CoreBox

@Component
class SearchStoreRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : SearchStoreRepository {
    val searchStoreIndex = "search-store"

    override fun findBox(box: CoreBox): List<SearchStore> {
        val mongoBox: Shape =
            Box(
                GeoJsonPoint(box.topLeft.lng, box.topLeft.lat),
                GeoJsonPoint(box.bottomRight.lng, box.bottomRight.lat),
            )

        val query = Query()
        query.addCriteria(
            Criteria
                .where("location")
                .within(mongoBox),
        )
        return mongoTemplate.find(query, SearchStoreEntity::class.java).map {
            it.to()
        }
    }

    /**
     * {
     *       "$search": {
     *         "index": "search-store",
     *         "compound": {
     *           "filter": [
     *             {
     *               "geoWithin": {
     *                 "circle": {
     *                   "center": {
     *                     "type": "Point",
     *                     "coordinates": [lng, lat]
     *                   },
     *                   "radius": {
     *                     "$numberDouble": "2000"
     *                   }
     *                 },
     *                 "path": "location"
     *               }
     *             },
     *             // 선택적으로 조건 추가 (category 존재할 경우에만)
     *             {
     *               "equals": {
     *                 "query": "카페",
     *                 "path": "category"
     *               }
     *             },
     *             // 선택적으로 조건 추가 (time 존재할 경우에만)
     *             {
     *               "range": {
     *                 "path": "openTime",
     *                 "lte": "2025-05-18T12:00:00Z"
     *               }
     *             },
     *             {
     *               "range": {
     *                 "path": "closeTime",
     *                 "gte": "2025-05-18T12:00:00Z"
     *               }
     *             },
     *             // 선택적으로 조건 추가 (status 존재할 경우에만)
     *             {
     *               "equals": {
     *                 "value": 1,
     *                 "path": "status"
     *               }
     *             }
     *           ],
     *           "must": [
     *             {
     *               "text": {
     *                 "query": "검색어",
     *                 "path": ["storeName", "menuName", "category"]
     *               }
     *             }
     *           ]
     *         },
     *         "score": {
     *           "function": {
     *             "distance": {
     *               "origin": {
     *                 "type": "Point",
     *                 "coordinates": [lng, lat]
     *               },
     *               "path": "location"
     *             }
     *           }
     *         }
     *       }
     *     }
     */
    override fun searchStore(
        lng: Double,
        lat: Double,
        maxDistance: Double,
        searchFilter: SearchFilter?,
        page: Int,
        size: Int,
    ): List<SearchStore> {
        val searchOp =
            makeSearchQuery(
                lng = lng,
                lat = lat,
                maxDistance = maxDistance,
                searchFilter = searchFilter,
            )
        val skipOp = Aggregation.skip(page.toLong() * size)
        val limitOp = Aggregation.limit(size.toLong())

        val pipeline =
            Aggregation.newAggregation(
                searchOp,
                skipOp,
                limitOp,
            )

        val result =
            mongoTemplate
                .aggregate(
                    pipeline,
                    "searchStore",
                    SearchStoreEntity::class.java,
                ).mappedResults

        return result.map {
            it.to()
        }
    }

    /**
     * TODO 검색어 자동완성 로직 수정 필요 -> DB를 탈지? -> 리서치 후 수정 일단 임시로...
     */
    override fun searchStoreRecommend(
        keyword: String,
        size: Int,
    ): List<String> {
        val searchOp = getAutocompleteOperation(keyword)
        val limitOp = Aggregation.limit(size.toLong())

        val pipeline =
            Aggregation.newAggregation(
                searchOp,
                limitOp,
            )

        val searchResult =
            mongoTemplate
                .aggregate(
                    pipeline,
                    "searchStore",
                    SearchStoreEntity::class.java,
                ).mappedResults

        // 검색어 자동완성 결과 리턴
        val result =
            searchResult.map {
                it.storeName
            }

        return result
    }

    fun makeSearchQuery(
        lng: Double,
        lat: Double,
        maxDistance: Double,
        searchFilter: SearchFilter?,
    ): AggregationOperation {
        val filters = mutableListOf<Bson>()
        val must = mutableListOf<Bson>()

        // 필수: 위치 기반 필터
        filters.add(
            Document(
                "geoWithin",
                Document(
                    "circle",
                    Document(
                        "center",
                        Document("type", "Point").append(
                            "coordinates",
                            listOf(lng, lat),
                        ),
                    ),
                ).append("radius", maxDistance),
            ).append("path", "location"),
        )

        // 선택적: 카테고리 필터
        searchFilter?.category?.let {
            filters.add(
                Document(
                    "text",
                    Document("query", it)
                        .append("path", "category"),
                ),
            )
        }

        // 선택적: 시간 필터
        searchFilter?.time?.let {
            filters.add(
                Document(
                    "range",
                    Document("path", "openTime")
                        .append("lte", it),
                ),
            )
            filters.add(
                Document(
                    "range",
                    Document("path", "closeTime")
                        .append("gte", it),
                ),
            )
        }

        // 선택적: 상태 필터
        searchFilter?.status?.let {
            if (it == StoreStatus.OPEN.statusCode) {
                filters.add(
                    Document(
                        "equals",
                        Document("value", true)
                            .append("path", "open"),
                    ),
                )
            }
        }

        // 선택적: 검색어 필터
        searchFilter?.searchText?.let {
            must.add(
                Document(
                    "text",
                    Document("query", it)
                        .append("path", listOf("storeName", "category")),
                ),
            )
        }

        // MongoDB Atlas Search 쿼리 생성
        val score =
            Document(
                "score",
                Document(
                    "function",
                    Document(
                        "distance",
                        Document(
                            "origin",
                            Document("type", "Point")
                                .append("coordinates", listOf(lng, lat)),
                        ).append("path", "location"),
                    ),
                ),
            )
        val searchQuery =
            Document(
                "\$search",
                Document()
                    .append("index", searchStoreIndex)
                    .append(
                        "compound",
                        Document("filter", filters)
                            .append("must", must),
                    ).append("score", score),
            )

        return AggregationOperation { _: AggregationOperationContext -> searchQuery }
    }

    fun getAutocompleteOperation(prefix: String): AggregationOperation =
        AggregationOperation {
            Document(
                "\$search",
                Document()
                    .append("index", "store-autocomplete-index")
                    .append(
                        "autocomplete",
                        Document()
                            .append("query", prefix)
                            .append("path", "storeName")
                            .append("fuzzy", Document("maxEdits", 1)), // 오타 허용
                    ),
            )
        }
}
