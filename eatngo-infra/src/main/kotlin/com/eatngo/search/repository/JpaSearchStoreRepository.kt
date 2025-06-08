package com.eatngo.search.repository

import com.eatngo.search.dto.SearchStoreFoodTypeDto
import com.eatngo.search.dto.SearchStoreRdbDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface JpaSearchStoreRepository : JpaRepository<SearchStoreRdbDto, Long> {
    @Query(
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
    )
    fun findByStoreId(storeId: Long): SearchStoreRdbDto

    @Query(
        """
            SELECT
                p.storeId AS storeId,
                p.foodTypes AS foodType
            FROM ProductEntity p
            WHERE p.updatedAt > :pivotTime
""",
    )
    fun findFoodTypesByProductUpdatedAt(pivotTime: LocalDateTime): List<SearchStoreFoodTypeDto>
}
