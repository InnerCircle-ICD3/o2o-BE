package com.eatngo.store.domain

import com.eatngo.common.constant.StoreEnum
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.LocalDateTime

/**
 * 매장(가게) 도메인 모델
 */
data class Store(
    val id: Long,                       // 매장의 고유 id
    val storeOwnerId: String,           // 해당 매장을 소유한 점주계정 id(매장:계정 1:1)
    val name: String,                   // 매장명
    val description: String?,           // 매장 설명
    val address: Address,               // 매장 주소
    val businessNumber: String,         // 사업자등록 번호
    val contactNumber: String?,         // 매장 or 점주 전화번호
    val imageUrl: String?,              // 대표 이미지 url(카드뷰에 보이는 이미지)
    val businessHours: List<BusinessHour?>?, // 영업시간(픽업시간과는 다름, 단순 정보제공용 및 확장성 고려해 추가)
    val storeCategory: List<String>,     // 매장의 카테고리(ex. 빵, 카페, 분식 ...)
    val foodCategory : List<String>?,     // 음식의 카테고리(ex. 햄버거, 소금빵, 모카빵 ...)
    val status: StoreEnum.StoreStatus = StoreEnum.StoreStatus.PENDING, // 매장 상태(기본: 승인대기중)
    val pickupStartTime: LocalTime,     // 픽업 시작 시간 (필수) (HH:mm)
    val pickupEndTime: LocalTime,       // 픽업 종료 시간 (필수) (HH:mm)
    val pickupAvailableForTomorrow: Boolean = false, // 내일 픽업 가능 여부(확장성 고려한 필드)
    val ratingAverage: Double = 0.0,    // 평균 별점
    val ratingCount: Int = 0,           // 별점 개수
    val createdAt: LocalDateTime,       // 생성일
    val updatedAt: LocalDateTime,       // 수정일
    var deletedAt: LocalDateTime? = null, // 삭제일(softDel용, 삭제 시간이 존재하면 softDel)
) {
    companion object {
        fun create(
            storeOwnerId: String,
            name: String,
            address: Address,
            businessNumber: String,
            pickupStartTime: LocalTime,
            pickupEndTime: LocalTime,
            description: String? = null,
            contactNumber: String? = null,
            imageUrl: String? = null,
            businessHours: List<BusinessHour?>? = null,
            storeCategory: List<String>,
            foodCategory : List<String>? = emptyList(),
            status: StoreEnum.StoreStatus = StoreEnum.StoreStatus.PENDING,
            pickupAvailableForTomorrow: Boolean = false,
            ratingAverage: Double = 0.0,
            ratingCount: Int = 0,
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = createdAt,
            deletedAt: LocalDateTime? = null
        ): Store {
            return Store(
                id = 0L,
                storeOwnerId = storeOwnerId,
                name = name,
                description = description,
                address = address,
                businessNumber = businessNumber,
                contactNumber = contactNumber,
                imageUrl = imageUrl,
                businessHours = businessHours,
                storeCategory = storeCategory,
                foodCategory = foodCategory,
                status = status,
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                pickupAvailableForTomorrow = pickupAvailableForTomorrow,
                ratingAverage = ratingAverage,
                ratingCount = ratingCount,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }

    /**
     * 매장 정보 업데이트
     */
    fun update(
        name: String? = null,
        description: String? = null,
        address: Address? = null,
        businessNumber: String? = null,
        contactNumber: String? = null,
        imageUrl: String? = null,
        businessHours: List<BusinessHour?>? = null,
        storeCategory: List<String>,
        foodCategory : List<String>? = emptyList(),
        pickupStartTime: LocalTime? = null,
        pickupEndTime: LocalTime? = null,
        pickupAvailableForTomorrow: Boolean? = null
    ): Store {
        return Store(
            id = id,
            storeOwnerId = this.storeOwnerId,
            name = name ?: this.name,
            description = description ?: this.description,
            address = address ?: this.address,
            businessNumber = businessNumber ?: this.businessNumber,
            contactNumber = contactNumber ?: this.contactNumber,
            imageUrl = imageUrl ?: this.imageUrl,
            businessHours = businessHours ?: this.businessHours,
            storeCategory = storeCategory,
            foodCategory = foodCategory ?: this.foodCategory,
            status = this.status,
            pickupStartTime = pickupStartTime ?: this.pickupStartTime,
            pickupEndTime = pickupEndTime ?: this.pickupEndTime,
            pickupAvailableForTomorrow = pickupAvailableForTomorrow ?: this.pickupAvailableForTomorrow,
            ratingAverage = this.ratingAverage,
            ratingCount = this.ratingCount,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now(),
            deletedAt = this.deletedAt
        )
    }

    /**
     * Soft Delete를 위한 메서드
     */
    fun softDelete(): Store {
        val now = LocalDateTime.now()
        this.deletedAt = now
        return this
    }

    /**
     * 현재 픽업 가능한지 확인 (매장 상태와 픽업 시간 기준)
     * 매장이 OPEN 상태이고 현재 시간이 픽업 가능 시간 내에 있는지 확인
     */
    fun isAvailableForPickup(now: LocalDateTime = LocalDateTime.now()): Boolean {
        // 매장 상태가 OPEN이 아니면 픽업 불가
        if (status != StoreEnum.StoreStatus.OPEN) {
            return false
        }

        val currentTime = now.toLocalTime()
        val result = if (pickupEndTime.isBefore(pickupStartTime)) {
            currentTime.isAfter(pickupStartTime) || currentTime.isBefore(pickupEndTime)
        } else {
            currentTime.isAfter(pickupStartTime) && currentTime.isBefore(pickupEndTime)
        }

        // 픽업 시간 내인지 확인
        return result
    }

    /**
     * 매장 상태만 업데이트
     */
    fun updateStatus(newStatus: StoreEnum.StoreStatus): Store {
        return this.copy(
            status = newStatus,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 픽업 정보만 업데이트
     */
    fun updatePickupInfo(
        startTime: LocalTime,
        endTime: LocalTime,
        availableForTomorrow: Boolean
    ): Store {
        return this.copy(
            pickupStartTime = startTime,
            pickupEndTime = endTime,
            pickupAvailableForTomorrow = availableForTomorrow
        )
    }
}

/**
 * 매장 주소 정보
 */
data class Address(
    val roadAddress: RoadAddress,           // 도로명 주소 (필수)
    val legalAddress: LegalAddress? = null, // 법정동 주소
    val adminAddress: AdminAddress? = null, // 행정동 주소
    val latitude: Double,                   // 위도
    val longitude: Double                   // 경도
)

/**
 * 영업시간 정보 (정보 표시용)
 */
data class BusinessHour(
    val dayOfWeek: DayOfWeek,
    val openTime: LocalTime,
    val closeTime: LocalTime
)

/**
 * 도로명 주소
 */
data class RoadAddress(
    val fullAddress: String,      // 전체 도로명 주소
    val zoneNo: String,           // 우편번호
)

/**
 * 법정동 주소
 */
data class LegalAddress(
    val fullAddress: String,              // 전체 지번 주소
)

/**
 * 행정동 주소
 */
data class AdminAddress(
    val fullAddress: String?       // 전체 행정동 주소
)