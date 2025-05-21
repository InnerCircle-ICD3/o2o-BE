package com.eatngo.store.domain

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.vo.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.LocalDateTime

/**
 * 매장(가게) 도메인 모델
 */
class Store(
    val id: Long,                       // 매장의 고유 id
    val storeOwnerId: Long,             // 해당 매장을 소유한 점주계정 id(매장:계정 1:1)
    val name: StoreName,                // 매장명
    val description: Description?,           // 매장 설명
    val address: Address,               // 매장 주소
    val businessNumber: BusinessNumber, // 사업자등록 번호
    val contactNumber: ContactNumber?,  // 매장 or 점주 전화번호
    val imageUrl: ImageUrl?,            // 대표 이미지 url(카드뷰에 보이는 이미지)
    val businessHours: List<BusinessHour>?, // 영업시간(픽업시간과는 다름, 단순 정보제공용 및 확장성 고려해 추가)
    val storeCategoryInfo: StoreCategoryInfo, // 매장의 카테고리 정보들
    var status: StoreEnum.StoreStatus = StoreEnum.StoreStatus.PENDING, // 매장 상태(기본: 승인대기중)
    var pickUpInfo: PickUpInfo,         // 픽업과 관련된 정보(픽업시간 from to, 내일 픽업 가능 여부)
    val reviewInfo: ReviewInfo,         // 리뷰와 관련된 정보(평균 별점, 리뷰 개수)
    val createdAt: LocalDateTime,       // 생성일
    var updatedAt: LocalDateTime,       // 수정일
    var deletedAt: LocalDateTime? = null, // 삭제일(softDel용, 삭제 시간이 존재하면 softDel)
) {
    companion object {
        fun create(
            storeOwnerId: Long,
            name: String,
            address: Address,
            businessNumber: String,
            description: String? = null,
            contactNumber: String? = null,
            imageUrl: String? = null,
            businessHours: List<BusinessHour>? = emptyList(),
            storeCategoryInfo: StoreCategoryInfo,
            pickUpInfo: PickUpInfo,
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = createdAt,
            deletedAt: LocalDateTime? = null
        ): Store {
            return Store(
                id = 0L,
                storeOwnerId = storeOwnerId,
                name = StoreName.from(name),
                description = Description.from(description),
                address = address,
                businessNumber = BusinessNumber.from(businessNumber),
                contactNumber = contactNumber?.let { ContactNumber.from(it) },
                imageUrl = imageUrl?.let { ImageUrl.from(it) },
                businessHours = businessHours,
                storeCategoryInfo = storeCategoryInfo,
                status = StoreEnum.StoreStatus.PENDING,
                pickUpInfo = pickUpInfo,
                reviewInfo = ReviewInfo(),
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
        contactNumber: String? = null,
        imageUrl: String? = null,
        businessHours: List<BusinessHour>? = emptyList(),
        storeCategoryInfo: StoreCategoryInfo,
        pickUpInfo: PickUpInfo
    ): Store {
        return Store(
            id = id,
            storeOwnerId = this.storeOwnerId,
            name = name?.let { StoreName.from(it) } ?: this.name,
            description = description?.let { Description.from(it) }  ?: this.description,
            address = address ?: this.address,
            businessNumber = this.businessNumber,
            contactNumber = contactNumber?.let { ContactNumber.from(it) } ?: this.contactNumber,
            imageUrl = imageUrl?.let { ImageUrl.from(it) } ?: this.imageUrl,
            businessHours = businessHours ?: this.businessHours,
            storeCategoryInfo = storeCategoryInfo,
            status = this.status,
            pickUpInfo = pickUpInfo,
            reviewInfo = this.reviewInfo,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now(),
            deletedAt = this.deletedAt
        )
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
        val result = if (pickUpInfo.pickupEndTime.isBefore(pickUpInfo.pickupStartTime)) {
            currentTime.isAfter(pickUpInfo.pickupStartTime) || currentTime.isBefore(pickUpInfo.pickupEndTime)
        } else {
            currentTime.isAfter(pickUpInfo.pickupStartTime) && currentTime.isBefore(pickUpInfo.pickupEndTime)
        }

        return result
    }

    /**
     * 매장 상태만 업데이트
     */
    fun updateStatus(newStatus: StoreEnum.StoreStatus) {
        status = newStatus
        updatedAt = LocalDateTime.now()
    }

    /**
     * 픽업 정보만 업데이트
     */
    fun updatePickupInfo(
        startTime: LocalTime,
        endTime: LocalTime,
        availableForTomorrow: Boolean
    ) {
        pickUpInfo = PickUpInfo(
            pickupStartTime = startTime,
            pickupEndTime = endTime,
            pickupAvailableForTomorrow = availableForTomorrow
        )
        updatedAt = LocalDateTime.now()
    }


    /**
     * Soft Delete를 위한 메서드
     */
    fun softDelete() {
        updatedAt = LocalDateTime.now()
        deletedAt = LocalDateTime.now()
    }
}

/**
 * 매장 위치 정보
 */
data class Address(
    val roadAddress: RoadAddress,           // 도로명 주소 (필수)
    val legalAddress: LegalAddress? = null, // 법정동 주소
    val adminAddress: AdminAddress? = null, // 행정동 주소
    val coordinate: Coordinate              // 위도, 경도
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
    val fullAddress: FullAddressVO,      // 전체 도로명 주소
    val zoneNo: ZoneNoVO,           // 우편번호
)

/**
 * 법정동 주소
 */
data class LegalAddress(
    val fullAddress: String?,     // 전체 지번 주소
)

/**
 * 행정동 주소
 */
data class AdminAddress(
    val fullAddress: String?       // 전체 행정동 주소
)

/**
 *  위도, 경도 좌표
 */
data class Coordinate(
    val latitude: Double,          // 위도
    val longitude: Double          // 경도
)

/**
 * 픽업과 관련된 정보
 */
data class PickUpInfo(
    val pickupStartTime: LocalTime,     // 픽업 시작 시간 (필수) (HH:mm)
    val pickupEndTime: LocalTime,       // 픽업 종료 시간 (필수) (HH:mm)
    val pickupAvailableForTomorrow: Boolean = false, // 내일 픽업 가능 여부(확장성 고려한 필드)
)

/**
 * 리뷰 정보
 */
data class ReviewInfo(
    val ratingAverage: Double = 0.0,    // 평균 별점
    val ratingCount: Int = 0,           // 별점 개수
)

/**
 * 매장의 카테고리 정보(분류와 사용자 입력 카테고리)
 */
data class StoreCategoryInfo(
    val storeCategory: List<StoreCategory>,    // 매장의 카테고리(ex. 빵, 카페, 분식 ...)
    val foodCategory: List<FoodCategory>?     // 음식의 카테고리(ex. 햄버거, 소금빵, 모카빵 ...)
)