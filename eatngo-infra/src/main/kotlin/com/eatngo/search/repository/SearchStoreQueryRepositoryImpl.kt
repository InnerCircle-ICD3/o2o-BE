package com.eatngo.search.repository

import com.eatngo.search.dto.SearchStoreFoodTypeDto
import com.eatngo.search.dto.SearchStoreRdbDto
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SearchStoreQueryRepositoryImpl(
    @PersistenceContext private val em: EntityManager,
) : SearchStoreQueryRepository {
    override fun findByStoreId(storeId: Long): SearchStoreRdbDto =
        em
            .createQuery(
                """
            SELECT SearchStoreRdbDto(
                s.id,
                s.name,
                s.imageUrl,
                s.storeCategory,
                s.foodCategory,
                p.foodTypes,
                s.address.roadNameAddress,
                s.address.latitude,
                s.address.longitude,
                p.status,
                s.status,
                s.businessHours,
                s.updatedAt,
                s.createdAt
            )
            FROM StoreJpaEntity s 
                JOIN FETCH s.address
                JOIN ProductEntity p ON p.storeId = s.id
            WHERE s.id = :storeId
        """,
                SearchStoreRdbDto::class.java,
            ).setParameter("storeId", storeId)
            .singleResult

    override fun findFoodTypesByProductUpdatedAt(pivotTime: LocalDateTime): List<SearchStoreFoodTypeDto> =
        em
            .createQuery(
                """
            SELECT SearchStoreFoodTypeDto(p.storeId, p.foodTypes)
            FROM ProductEntity p
            WHERE p.updatedAt > :pivotTime
        """,
                SearchStoreFoodTypeDto::class.java,
            ).setParameter("pivotTime", pivotTime)
            .resultList
}
