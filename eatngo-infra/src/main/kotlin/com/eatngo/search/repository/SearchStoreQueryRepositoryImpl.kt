package com.eatngo.search.repository

import com.eatngo.common.type.CoordinateVO
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
                SELECT new com.eatngo.search.dto.SearchStoreRdbDto(
                    s.id,
                    s.name,
                    s.imageUrl,
                    s.storeCategory,
                    s.foodCategory,
                    null,
                    s.address.roadNameAddress,
                    s.address.latitude,
                    s.address.longitude,
                    s.status,
                    s.businessHours,
                    s.updatedAt,
                    s.createdAt
                )
                FROM StoreJpaEntity s 
                LEFT JOIN s.address a
                WHERE s.id = :storeId
                """.trimIndent(),
                SearchStoreRdbDto::class.java,
            ).setParameter("storeId", storeId)
            .singleResult

    override fun findAddressByStoreId(storeId: Long): CoordinateVO =
        em
            .createNativeQuery(
                """
                SELECT a.latitude,
                       a.longitude
                FROM store_address a
                WHERE a.id = :storeId
                """.trimIndent(),
            ).setParameter("storeId", storeId)
            .singleResult
            .let { result ->
                val (latitude, longitude) = result as Array<*>
                CoordinateVO.from(latitude as Double, longitude as Double)
            }

    override fun findFoodTypesByStoreIds(storeIds: List<Long>): List<SearchStoreFoodTypeDto> =
        em
            .createQuery(
                """
            SELECT new com.eatngo.search.dto.SearchStoreFoodTypeDto(p.storeId, f)
            FROM ProductEntity p
            JOIN p.foodTypes f
            WHERE p.storeId IN :storeIds
        """,
                SearchStoreFoodTypeDto::class.java,
            ).setParameter("storeIds", storeIds)
            .resultList

    override fun findStoresByUpdateAt(pivotTime: LocalDateTime): List<SearchStoreRdbDto> =
        em
            .createQuery(
                """
                SELECT new com.eatngo.search.dto.SearchStoreRdbDto(
                    s.id,
                    s.name,
                    s.imageUrl,
                    s.storeCategory,
                    s.foodCategory,
                    null,
                    s.address.roadNameAddress,
                    s.address.latitude,
                    s.address.longitude,
                    s.status,
                    s.businessHours,
                    s.updatedAt,
                    s.createdAt
                )
                FROM StoreJpaEntity s 
                    JOIN s.address
                WHERE s.updatedAt > :pivotTime
                """.trimIndent(),
                SearchStoreRdbDto::class.java,
            ).setParameter("pivotTime", pivotTime)
            .resultList
}
