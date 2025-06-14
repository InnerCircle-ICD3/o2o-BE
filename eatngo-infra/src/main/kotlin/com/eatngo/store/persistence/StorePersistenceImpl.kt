package com.eatngo.store.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.dto.BusinessHourDto
import com.eatngo.store.dto.StoreSchedulerDto
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.rdb.entity.StoreJpaEntity
import com.eatngo.store.rdb.repository.StoreRdbRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.DayOfWeek

/**
 * 매장 영속성 구현체
 */
@Component
class StorePersistenceImpl(
    private val storeRdbRepository: StoreRdbRepository,
) : StorePersistence {
    companion object {
        private val objectMapper = jacksonObjectMapper()
    }
    @SoftDeletedFilter
    override fun findById(id: Long): Store? =
        storeRdbRepository
            .findById(id)
            .map(StoreJpaEntity::toStore)
            .orElse(null)

    @SoftDeletedFilter
    override fun findAllByIds(storeIds: List<Long>): List<Store> =
        storeRdbRepository
            .findAllByIdIn(storeIds)
            .map { StoreJpaEntity.toStore(it) }

    @SoftDeletedFilter
    override fun findByOwnerId(storeOwnerId: Long): List<Store> =
        storeRdbRepository
            .findByStoreOwnerIdWithAddress(storeOwnerId)
            .map { StoreJpaEntity.toStore(it) }

    override fun save(store: Store): Store =
        StoreJpaEntity.toStore(
            storeRdbRepository.save(
                StoreJpaEntity.from(store),
            ),
        )

    override fun updateStatus(storeId: Long, status: StoreEnum.StoreStatus): Boolean =
        storeRdbRepository.updateStatus(storeId, status) > 0

    override fun deleteById(id: Long): Boolean =
        storeRdbRepository.softDeleteById(id) > 0

    override fun batchUpdateStatusToClosed(storeIds: List<Long>): Int =
        storeRdbRepository.batchUpdateStatusToClosed(storeIds)


    @SoftDeletedFilter
    override fun findOpenStoresForScheduler(
        dayOfWeek: String,
        startTime: LocalTime,
        endTime: LocalTime
    ): List<StoreSchedulerDto> =
        storeRdbRepository
            .findOpenStoresForScheduler(dayOfWeek, startTime, endTime)
            .map { projection ->
                StoreSchedulerDto(
                    id = projection.id,
                    businessHours = parseBusinessHoursJson(projection.businessHours),
                    status = projection.status
                )
            }

    private fun parseBusinessHoursJson(json: String): List<BusinessHourDto> {
        val typeRef = object : TypeReference<List<Map<String, String>>>() {}

        return objectMapper.readValue(json, typeRef).map { map ->
            BusinessHourDto(
                dayOfWeek = DayOfWeek.valueOf(map["dayOfWeek"]!!),
                openTime = LocalTime.parse(map["openTime"]!!),
                closeTime = LocalTime.parse(map["closeTime"]!!)
            )
        }
    }
}
