package com.eatngo.mongo.repository.search

import com.eatngo.mongo.entity.search.SearchStoreEntity
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreStatus
import com.eatngo.search.dto.AutoCompleteStoreNameDto
import com.eatngo.search.dto.SearchFilter
import com.eatngo.search.dto.SearchStoreWithDistance
import com.eatngo.search.infra.SearchStoreRepository
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.data.geo.Box
import org.springframework.data.geo.GeoResults
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.geo.Shape
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.time.LocalDateTime
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
    ): List<SearchStoreWithDistance> {
        // 필수 : 위치 기반 필터링
        val point = Point(longitude, latitude)
        val nearQuery =
            NearQuery
                .near(point, Metrics.KILOMETERS)
                .maxDistance(maxDistance) // km 단위로 사용
                .spherical(true)

        // 서브쿼리 생성
        val query = Query()
        // 선택 : 카테고리 필터링
        searchFilter.storeCategory?.let {
            query.addCriteria(
                Criteria.where("storeCategory").`is`(it),
            )
        }

        // 선택 : 픽업 가능 시간 필터링
        val now = LocalDateTime.now()
        val currentDayOfWeek = now.dayOfWeek
        val currentTime = now.toLocalTime()
        searchFilter.time?.let {
            query.addCriteria(
                Criteria
                    .where("pickupHour.openTime")
                    .lte(currentTime)
                    .and("pickupHour.closeTime")
                    .gt(currentTime),
            )
        }

        // 선택 : 예약 가능 상태 필터링 -> TODO 로직 확인 필요...(매장 오픈 시간과 상태로 예약 가능 상태 필터링)
        searchFilter.status?.let {
            query.addCriteria(
                Criteria
                    .where("businessHours.$currentDayOfWeek.openTime")
                    .lte(currentTime)
                    .and("businessHours.$currentDayOfWeek.closeTime")
                    .gt(currentTime),
            )

            query.addCriteria(
                Criteria.where("status").`is`(SearchStoreStatus.from(it).code),
            )
        }

        // Criteria가 있으면 NearQuery에 붙여주기
        nearQuery.query(query)

        val geoResults: GeoResults<SearchStoreEntity> = mongoTemplate.geoNear(nearQuery, SearchStoreEntity::class.java)
        return geoResults.content.map {
            SearchStoreWithDistance(
                store = it.content.to(),
                distance = it.distance?.value ?: 0.0, // 거리 값이 없을 경우 0.0으로 처리
            )
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
