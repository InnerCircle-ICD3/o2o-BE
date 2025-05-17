package com.eatngo.mongo.repository.search

import com.eatngo.mongo.entity.search.SearchStoreEntity
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.infra.SearchStoreRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class SearchStoreRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
): SearchStoreRepository {
    override fun findByLocation(
        lng: Double,
        lat: Double,
        maxDistance: Double
    ): List<SearchStore> {
        val query = Query()
        query.addCriteria(
            Criteria.where("location")
                .near(GeoJsonPoint(lng, lat))
                .maxDistance(maxDistance)
        )
        return mongoTemplate.find(query, SearchStoreEntity::class.java).map {
            it.to()
        }
    }
}