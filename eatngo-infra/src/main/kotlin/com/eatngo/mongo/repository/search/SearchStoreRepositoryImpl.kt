package com.eatngo.mongo.repository.search

import com.eatngo.mongo.entity.search.SearchStoreEntity
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.AutoCompleteStoreNameDto
import com.eatngo.search.dto.SearchFilter
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
                GeoJsonPoint(box.topLeft.longitude, box.topLeft.latitude),
                GeoJsonPoint(box.bottomRight.longitude, box.bottomRight.latitude),
            )

        val query = Query()
        query.addCriteria(
            Criteria
                .where("coordinate")
                .within(mongoBox),
        )

        val result = mongoTemplate.find(query, SearchStoreEntity::class.java)

        return result.map {
            it.to()
        }
    }

    override fun listStore(
        longitude: Double,
        latitude: Double,
        maxDistance: Double,
        searchFilter: SearchFilter,
        page: Int,
        size: Int,
    ): List<SearchStore> {
        val query = Query()
        // 필수 : 위치 기반 필터링
        query.addCriteria(
            Criteria
                .where("coordinate")
                .nearSphere(GeoJsonPoint(longitude, latitude))
                .maxDistance(maxDistance),
        )

        // 선택 : 카테고리 필터링
        searchFilter.storeCategory?.let {
            query.addCriteria(
                Criteria.where("storeCategory").`is`(it),
            )
        }

        // TODO 선택 : 시간 필터링
        // TODO 선택 : 매장 상태 필터링

        val result = mongoTemplate.find(query, SearchStoreEntity::class.java)
        return result.map {
            it.to()
        }
    }

    override fun searchStore(
        longitude: Double,
        latitude: Double,
        maxDistance: Double,
        searchText: String,
        page: Int,
        size: Int,
    ): List<SearchStore> {
        val searchOp =
            makeSearchQuery(
                longitude = longitude,
                latitude = latitude,
                maxDistance = maxDistance,
                searchText = searchText,
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
     * 검색어 자동완성 기능을 위한 MongoDB Atlas Search 쿼리
     * @param keyword 검색어
     * @param size 결과 개수
     * @return 자동완성된 매장 이름 리스트
     */
    override fun autocompleteStoreName(
        keyword: String,
        size: Int,
    ): List<AutoCompleteStoreNameDto> {
        val searchOp = getAutocompleteOperation(keyword)
        val limitOp = Aggregation.limit(size.toLong())
        val projectionOp =
            Aggregation.project("_id", "storeName") // _id, storeName 필드만 추출

        val pipeline =
            Aggregation.newAggregation(
                searchOp,
                limitOp,
                projectionOp,
            )

        val result =
            mongoTemplate
                .aggregate(
                    pipeline,
                    "searchStore",
                    SearchStoreEntity::class.java,
                ).mappedResults

        return result.map {
            AutoCompleteStoreNameDto.from(
                storeId = it.storeId,
                storeName = it.storeName,
            )
        }
    }

    fun makeSearchQuery(
        longitude: Double,
        latitude: Double,
        maxDistance: Double,
        searchText: String,
    ): AggregationOperation {
        val filters = mutableListOf<Bson>()
        val must = mutableListOf<Bson>()

        // 위치 기반 필터
        filters.add(
            Document(
                "geoWithin",
                Document(
                    "circle",
                    Document()
                        .append(
                            "center",
                            Document()
                                .append("type", "Point")
                                .append("coordinates", listOf(longitude, latitude)),
                        ).append("radius", maxDistance), // meter 단위
                ).append("path", "coordinate"),
            ),
        )

        // 검색어 필터
        must.add(
            Document(
                "text",
                Document("query", searchText)
                    .append("path", listOf("storeName", "foodCategory")),
            ),
        )

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
                                .append("coordinates", listOf(longitude, latitude)),
                        ).append("path", "coordinate"),
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
