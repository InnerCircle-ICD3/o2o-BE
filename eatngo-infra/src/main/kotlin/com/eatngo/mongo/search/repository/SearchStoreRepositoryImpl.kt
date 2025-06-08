package com.eatngo.mongo.search.repository

import com.eatngo.mongo.search.dto.SearchStoreAutoCompleteDto
import com.eatngo.mongo.search.entity.SearchStoreEntity
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreFoodTypes
import com.eatngo.search.domain.SearchStoreStatus
import com.eatngo.search.dto.AutoCompleteStoreNameDto
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.SearchFilter
import com.eatngo.search.dto.SearchStoreWithDistance
import com.eatngo.search.infra.SearchStoreRepository
import org.bson.Document
import org.springframework.data.domain.Sort
import org.springframework.data.geo.GeoResults
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.geo.Shape
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SearchStoreRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : SearchStoreRepository {
    val searchStoreIndex = "search-store"
    val autoCompleteIndex = "store-auto-complete"

    override fun findBox(box: Box): List<SearchStore> {
        val mongoBox: Shape =
            org.springframework.data.geo.Box(
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
        val query = makeFilterQuery(searchFilter)
        // Criteria가 있으면 NearQuery에 붙여주기
        nearQuery.query(query)

        val geoResults: GeoResults<SearchStoreEntity> = mongoTemplate.geoNear(nearQuery, SearchStoreEntity::class.java)
        return geoResults.content.map {
            SearchStoreWithDistance(
                store = it.content.to(),
                distance = it.distance.value,
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
                maxDistanceKm = maxDistance,
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
                    "SearchStore",
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
            Aggregation
                .project("_id", "storeName") // _id, storeName 필드만 추출
                .andExpression("metaSearchScore")
                .`as`("score")
        val sortOp = Aggregation.sort(Sort.by(Sort.Direction.DESC, "score"))

        val pipeline =
            Aggregation.newAggregation(
                searchOp,
                sortOp,
                limitOp,
                projectionOp,
            )

        val result =
            mongoTemplate
                .aggregate(
                    pipeline,
                    "SearchStore",
                    SearchStoreAutoCompleteDto::class.java,
                ).mappedResults

        return result.map {
            AutoCompleteStoreNameDto.from(
                storeId = it.storeId,
                storeName = it.storeName,
            )
        }
    }

    override fun save(searchStore: SearchStore) {
        val store = SearchStoreEntity.from(searchStore)

        val query = Query(Criteria.where("_id").`is`(store.storeId))
        val update =
            Update()
                .set("storeName", store.storeName)
                .set("storeImage", store.storeImage)
                .set("storeCategory", store.storeCategory)
                .set("foodCategory", store.foodCategory)
                .set("foodTypes", store.foodTypes)
                .set("roadNameAddress", store.roadNameAddress)
                .set("coordinate", store.coordinate)
                .set("status", store.status)
                .set("businessHours", store.businessHours)
                .set("updatedAt", LocalDateTime.now())
                .set("createdAt", store.createdAt)

        mongoTemplate.upsert(query, update, SearchStoreEntity::class.java, "SearchStore")
    }

    override fun saveAll(searchStoreList: List<SearchStore>) {
        val bulkOps =
            mongoTemplate
                .bulkOps(
                    BulkOperations.BulkMode.UNORDERED,
                    SearchStoreEntity::class.java,
                    "SearchStore",
                )

        searchStoreList.map { searchStore ->
            val store = SearchStoreEntity.from(searchStore)

            val query = Query(Criteria.where("_id").`is`(store.storeId))
            val update =
                Update()
                    .set("storeName", store.storeName)
                    .set("storeImage", store.storeImage)
                    .set("storeCategory", store.storeCategory)
                    .set("foodCategory", store.foodCategory)
                    .set("roadNameAddress", store.roadNameAddress)
                    .set("coordinate", store.coordinate)
                    .set("status", store.status)
                    .set("businessHours", store.businessHours)
                    .set("updatedAt", LocalDateTime.now())
                    .set("createdAt", store.createdAt)

            bulkOps.upsert(
                query,
                update,
            )
        }

        bulkOps.execute()
    }

    override fun updateFoodTypesAll(foodTypeDataList: List<SearchStoreFoodTypes>) {
        val bulkOps =
            mongoTemplate
                .bulkOps(
                    BulkOperations.BulkMode.UNORDERED,
                    SearchStoreEntity::class.java,
                    "SearchStore",
                )
        foodTypeDataList.map { foodTypeData ->
            val query = Query(Criteria.where("_id").`is`(foodTypeData.storeId))
            val update =
                Update()
                    .set("foodTypes", foodTypeData.foodTypes)
                    .set("updatedAt", LocalDateTime.now())

            bulkOps.upsert(
                query,
                update,
            )
        }

        bulkOps.execute()
    }

    override fun deleteIds(deleteIds: List<Long>) {
        if (deleteIds.isEmpty()) return

        val query = Query(Criteria.where("_id").`in`(deleteIds))
        mongoTemplate.remove(query, SearchStoreEntity::class.java, "SearchStore")
    }

    override fun deleteId(deleteId: Long) {
        val query = Query(Criteria.where("_id").`is`(deleteId))
        mongoTemplate.remove(query, SearchStoreEntity::class.java, "SearchStore")
    }

    /**
     * status 정렬순을 보장하기 위해 status를 지정하여 검색
     */
    fun makeSearchQuery(
        longitude: Double,
        latitude: Double,
        maxDistanceKm: Double,
        searchText: String,
        status: Int = 1, // 기본적으로 OPEN 상태의 매장을 우선 검색
    ): AggregationOperation {
        val maxDistance = maxDistanceKm * 1000 // km 단위를 meter로 변환

        val must =
            listOf(
                Document(
                    "text",
                    Document("query", searchText)
                        .append("path", listOf("storeName", "roadNameAddress", "foodCategory"))
                        .append("score", Document("boost", Document("value", 1.0))),
                ),
                Document(
                    "equals",
                    Document("path", "status")
                        .append("value", status),
                ),
            )
        val filter =
            listOf( // 거리 필터 (maxDistance)
                Document(
                    "near",
                    Document("path", "coordinate")
                        .append("pivot", maxDistance)
                        .append(
                            "origin",
                            Document("type", "Point")
                                .append("coordinates", listOf(longitude, latitude)),
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
                        Document()
                            .append(
                                "must",
                                must,
                            ).append(
                                "filter",
                                filter,
                            ),
                    ),
            )

        return AggregationOperation { _: AggregationOperationContext -> searchQuery }
    }

    fun getAutocompleteOperation(prefix: String): AggregationOperation =
        AggregationOperation {
            Document(
                "\$search",
                Document()
                    .append("index", autoCompleteIndex)
                    .append(
                        "autocomplete",
                        Document()
                            .append("query", prefix)
                            .append("path", "storeName")
                            .append("fuzzy", Document("maxEdits", 1)), // 오타 허용
                    ),
            )
        }

    fun makeFilterQuery(searchFilter: SearchFilter): Query {
        val query = Query()
        // 선택 : 카테고리 필터링
        searchFilter.storeCategory?.let {
            query.addCriteria(
                Criteria.where("storeCategory").`is`(it),
            )
        }

        searchFilter.time?.let {
            // 선택 : 픽업 가능 시간 필터링
            val now = LocalDateTime.now()
            val currentDayOfWeek = now.dayOfWeek
            val currentTime = now.toLocalTime()

            val openTimeField = "businessHours.$currentDayOfWeek.openTime"
            val closeTimeField = "businessHours.$currentDayOfWeek.closeTime"
            query.addCriteria(
                Criteria
                    .where(openTimeField)
                    .lte(currentTime)
                    .and(closeTimeField)
                    .gt(currentTime),
            )
        }

        // 선택 : 예약 가능 상태 필터링 -> TODO 로직 확인 필요...(오픈 할 때 마다 예약 가능 상태 변경할건지)
        if (searchFilter.onlyReservable) {
            query.addCriteria(
                Criteria.where("status").`is`(SearchStoreStatus.OPEN.code),
            )
        }

        return query
    }
}
