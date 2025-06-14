package com.eatngo.mongo.search.repository

import com.eatngo.mongo.search.entity.SearchStoreEntity
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreFoodTypes
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.SearchFilter
import com.eatngo.search.infra.SearchStoreRepository
import org.bson.Document
import org.springframework.data.geo.Shape
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SearchStoreRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : SearchStoreRepository {
    val searchStoreIndex = "search-store"

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

    override fun searchStore(
        longitude: Double,
        latitude: Double,
        maxDistance: Double,
        searchFilter: SearchFilter,
        size: Int,
    ): List<SearchStore> {
        val searchOp =
            makeSearchQuery(
                longitude = longitude,
                latitude = latitude,
                maxDistanceKm = maxDistance,
                searchFilter = searchFilter,
            )
        val projectOp =
            AggregationOperation { context ->
                Document(
                    "\$project",
                    Document(
                        mapOf(
                            "storeId" to 1,
                            "storeName" to 1,
                            "storeImage" to 1,
                            "storeCategory" to 1,
                            "foodCategory" to 1,
                            "foodTypes" to 1,
                            "roadNameAddress" to 1,
                            "coordinate" to 1,
                            "status" to 1,
                            "businessHours" to 1,
                            "metaSearchScore" to Document("\$meta", "searchScore"), // 검색 점수
                            "paginationToken" to Document("\$meta", "searchSequenceToken"), // 검색 점수
                        ),
                    ),
                )
            }
        val limitOp = Aggregation.limit(size.toLong())

        val pipeline =
            Aggregation.newAggregation(
                searchOp,
                projectOp,
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
                    .set("foodTypes", store.foodTypes)
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

    override fun updateStoreStatus(
        storeId: Long,
        status: String,
    ) {
        val query = Query(Criteria.where("_id").`is`(storeId))
        val update =
            Update()
                .set("status", status)
                .set("updatedAt", LocalDateTime.now())

        mongoTemplate.updateFirst(query, update, SearchStoreEntity::class.java, "SearchStore")
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
        searchFilter: SearchFilter,
        status: Int = 1, // 기본적으로 OPEN 상태의 매장을 우선 검색
    ): AggregationOperation {
        val maxDistance = maxDistanceKm * 1000 // km 단위를 meter로 변환

        val must = mutableListOf<Document>()
        val filter = mutableListOf<Document>()

        // TODO: STATUS 필터링 로직 개선 필요 -> 예약 가능 상태 필터링
        must += (
            Document(
                "equals",
                Document("path", "status")
                    .append("value", status),
            )
        )
        // 검색어 필터링
        if (searchFilter.searchText != null && searchFilter.searchText != "") {
            must += (
                Document(
                    "text",
                    Document("query", searchFilter.searchText)
                        .append("path", listOf("storeName", "roadNameAddress", "foodCategory"))
                        .append("score", Document("boost", Document("value", 1.0))),
                )
            )
        }
        // 카테고리 필터링
        searchFilter.storeCategory?.let { storeCategory ->
            must += (
                Document(
                    "term",
                    Document("path", "storeCategory")
                        .append("query", storeCategory.name),
                )
            )
        }
        // 픽업 가능 시간 필터링
        searchFilter.time?.let {
            // 현재 시간 기준으로 오픈 중인 매장 필터링
            val now = LocalDateTime.now()
            val currentDayOfWeek = now.dayOfWeek

            must += (
                Document(
                    "range",
                    Document("path", "businessHours.$currentDayOfWeek.openTime")
                        .append("lte", searchFilter.time),
                )
            )
            must += (
                Document(
                    "range",
                    Document("path", "businessHours.$currentDayOfWeek.closeTime")
                        .append("gte", searchFilter.time),
                )
            )
        }

        // 거리 스코어링
        filter +=
            Document(
                "near",
                Document("path", "coordinate")
                    .append("pivot", maxDistance)
                    .append(
                        "origin",
                        Document("type", "Point")
                            .append("coordinates", listOf(longitude, latitude)),
                    ),
            )
        // 반경 조건 추가
        must +=
            Document(
                "geoWithin",
                Document()
                    .append("path", "coordinate")
                    .append(
                        "circle",
                        Document()
                            .append(
                                "center",
                                Document()
                                    .append("type", "Point")
                                    .append("coordinates", listOf(longitude, latitude)),
                            ).append("radius", maxDistance),
                    ),
            )

        val searchDocument =
            Document()
                .append("index", searchStoreIndex)
                .append(
                    "compound",
                    Document()
                        .append("must", must)
                        .append("filter", filter),
                )
        // Cursor pagination을 위한 searchAfter 설정
        if (searchFilter.lastId != null) {
            searchDocument.append(
                "searchAfter",
                searchFilter.lastId,
            )
        }

        val searchQuery =
            Document(
                "\$search",
                searchDocument,
            )

        return AggregationOperation { _: AggregationOperationContext -> searchQuery }
    }
}
