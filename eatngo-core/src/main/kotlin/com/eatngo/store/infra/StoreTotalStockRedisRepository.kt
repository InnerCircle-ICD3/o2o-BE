package com.eatngo.store.infra

import java.time.LocalDate

interface StoreTotalStockRedisRepository {
    /**
     * 매장의 특정 날짜 총 재고 수량을 설정
     * Key: {storeId}:{yyyyMMdd}:totalCount
     * TTL: 24시간
     */
    fun setStoreTotalStock(
        storeId: Long,
        date: LocalDate,
        totalStock: Int,
    )

    /**
     * 매장의 특정 날짜 총 재고 수량을 조회
     * @return 재고 수량 (null: 오늘 판매 안함 또는 데이터 없음)
     */
    fun getStoreTotalStock(
        storeId: Long,
        date: LocalDate,
    ): Int?

    /**
     * 매장의 특정 날짜 총 재고 수량을 조회
     * @return 재고 수량 맵
     */
    fun getStoreTotalStockMap(
        storeIdList: List<Long>,
        date: LocalDate,
    ): Map<Long, Int>

    /**
     * 매장의 재고 데이터 삭제
     */
    fun deleteStoreTotalStock(
        storeId: Long,
        date: LocalDate,
    )
}
