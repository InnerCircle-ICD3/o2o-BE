package com.eatngo.search.repository

import com.eatngo.search.dto.SearchStoreFoodTypeDto
import com.eatngo.search.dto.SearchStoreRdbDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface JpaSearchStoreRepository : JpaRepository<SearchStoreRdbDto, Long> {
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
