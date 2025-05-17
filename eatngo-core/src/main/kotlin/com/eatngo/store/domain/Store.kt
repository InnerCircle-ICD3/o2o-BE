package com.eatngo.store.domain

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.dto.AdminAddressDto
import com.eatngo.store.dto.BusinessHourDto
import com.eatngo.store.dto.LegalAddressDto
import com.eatngo.store.dto.LocationDto
import com.eatngo.store.dto.RoadAddressDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionSummary
import com.eatngo.store.dto.StoreSummary
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * 매장(가게) 도메인 모델
 */
class Store(
    val id: Long,                       // 매장의 고유 id
    val storeOwnerId: String,           // 해당 매장을 소유한 점주계정 id(매장:계정 1:1)
    val name: String,                   // 매장명
    val description: String?,           // 매장 설명
    val address: Address,               // 매장 주소
    val businessNumber: String,         // 사업자등록 번호
    val contactNumber: String?,         // 매장 or 점주 전화번호
    val imageUrl: String?,              // 대표 이미지 url(카드뷰에 보이는 이미지)
    val businessHours: List<BusinessHour?>?, // 영업시간(픽업시간과는 다름, 단순 정보제공용 및 확장성 고려해 추가)
    val categories: List<String?>?,     // 카테고리(ex. 햄버거, 소금빵, 모카빵 ...)
    val status: StoreEnum.StoreStatus = StoreEnum.StoreStatus.PENDING, // 매장 상태(기본: 승인대기중)
    val pickupStartTime: LocalTime,     // 픽업 시작 시간 (필수) (HH:mm)
    val pickupEndTime: LocalTime,       // 픽업 종료 시간 (필수) (HH:mm)
    val pickupAvailableForTomorrow: Boolean = false, // 내일 픽업 가능 여부(확장성 고려한 필드)
    val ratingAverage: Double = 0.0,    // 평균 별점
    val ratingCount: Int = 0,           // 별점 개수
    val createdAt: ZonedDateTime,       // 생성일
    val updatedAt: ZonedDateTime,       // 수정일
    var deletedAt: ZonedDateTime? = null, // 삭제일(softDel용, 삭제 시간이 존재하면 softDel)
) {
    /**
     * 영업시간 정보 반환 (정보 표시용)
     * 영업 시간은 단순히 정보성으로만 사용되며 매장의 활성화 여부에 영향을 주지 않음
     */
    fun getBusinessHoursInfo(dayOfWeek: DayOfWeek? = null): List<BusinessHour?>? {
        return if (dayOfWeek != null) {
            businessHours?.filter { it?.dayOfWeek == dayOfWeek }
        } else {
            businessHours
        }
    }
    
    /**
     * 현재 픽업 가능한지 확인 (매장 상태와 픽업 시간 기준)
     * 매장이 OPEN 상태이고 현재 시간이 픽업 가능 시간 내에 있는지 확인
     */
    fun isAvailableForPickup(now: ZonedDateTime = ZonedDateTime.now()): Boolean {
        // 매장 상태가 OPEN이 아니면 픽업 불가
        if (status != StoreEnum.StoreStatus.OPEN) {
            return false
        }
        
        val currentTime = now.toLocalTime()
        
        // 픽업 시간 내인지 확인
        return isWithinPickupHours(currentTime)
    }
    
    /**
     * 주어진 날짜에 픽업 가능한지 확인
     * 오늘 또는 내일 픽업 가능 여부를 확인
     */
    fun isAvailableForPickupOn(targetDate: ZonedDateTime, now: ZonedDateTime = ZonedDateTime.now()): Boolean {
        val today = now.toLocalDate()
        val targetDay = targetDate.toLocalDate()
        
        return when {
            // 오늘인 경우
            targetDay.isEqual(today) -> isAvailableForPickup(now)
            
            // 내일인 경우
            targetDay.isEqual(today.plusDays(1)) -> status == StoreEnum.StoreStatus.OPEN && pickupAvailableForTomorrow
            
            // 그 외의 경우는 불가능
            else -> false
        }
    }
    
    /**
     * 주어진 시간이 픽업 시간 내인지 확인
     */
    private fun isWithinPickupHours(time: LocalTime): Boolean {
        // 픽업 종료 시간이 시작 시간보다 이전이면 자정을 넘어가는 경우 (예: 22:00 ~ 02:00)
        return if (pickupEndTime.isBefore(pickupStartTime)) {
            time.isAfter(pickupStartTime) || time.isBefore(pickupEndTime)
        } else {
            time.isAfter(pickupStartTime) && time.isBefore(pickupEndTime)
        }
    }
    
    /**
     * Soft Delete를 위한 메서드
     */
    fun softDelete(): Store {
        val now = ZonedDateTime.now()
        this.deletedAt = now
        return this
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
        businessHours: List<BusinessHour>? = null,
        categories: List<String?>? = null,
        status: StoreEnum.StoreStatus? = null,
        pickupStartTime: LocalTime? = null,
        pickupEndTime: LocalTime? = null,
        pickupAvailableForTomorrow: Boolean? = null,
    ): Store {
        val updatedStore = Store(
            id = this.id,
            storeOwnerId = this.storeOwnerId,
            name = name ?: this.name,
            description = description ?: this.description,
            address = address ?: this.address,
            businessNumber = businessNumber ?: this.businessNumber,
            contactNumber = contactNumber ?: this.contactNumber,
            imageUrl = imageUrl ?: this.imageUrl,
            businessHours = businessHours ?: this.businessHours,
            categories = categories ?: this.categories,
            status = status ?: this.status,
            pickupStartTime = pickupStartTime ?: this.pickupStartTime,
            pickupEndTime = pickupEndTime ?: this.pickupEndTime,
            pickupAvailableForTomorrow = pickupAvailableForTomorrow ?: this.pickupAvailableForTomorrow,
            createdAt = this.createdAt,
            updatedAt = ZonedDateTime.now(),
            deletedAt = this.deletedAt
        )
        return updatedStore
    }
    
    /**
     * 매장 상태만 업데이트
     */
    fun updateStatus(newStatus: StoreEnum.StoreStatus): Store {
        return this.update(status = newStatus)
    }
    
    /**
     * 픽업 정보만 업데이트
     */
    fun updatePickupInfo(
        startTime: LocalTime? = null, 
        endTime: LocalTime? = null, 
        availableForTomorrow: Boolean? = null
    ): Store {
        return this.update(
            pickupStartTime = startTime, 
            pickupEndTime = endTime,
            pickupAvailableForTomorrow = availableForTomorrow
        )
    }

    /**
     * 상점 응답 DTO로 변환
     */
    fun toResponseDto(nowTime: ZonedDateTime = ZonedDateTime.now()): StoreDto {
        return StoreDto(
            storeId = id,
            name = name,
            roadAddress = RoadAddressDto.from(address.roadAddress),
            legalAddress = LegalAddressDto.from(address.legalAddress),
            adminAddress = AdminAddressDto.from(address.adminAddress),
            location = LocationDto(
                lat = address.latitude,
                lng = address.longitude
            ),
            businessNumber = businessNumber,
            businessHours = businessHours?.mapNotNull { businessHour ->
                businessHour?.let {
                    BusinessHourDto.from(it)
                }
            } ?: emptyList(),
            contact = contactNumber ?: "",
            description = description ?: "",
            pickupStartTime = pickupStartTime.toString(),
            pickupEndTime = pickupEndTime.toString(),
            pickupAvailableForTomorrow = pickupAvailableForTomorrow,
            mainImageUrl = imageUrl,
            status = status,
            isAvailableForPickup = isAvailableForPickup(nowTime),
            categories = categories?.filterNotNull() ?: emptyList(),
            ratingAverage = ratingAverage,
            ratingCount = ratingCount
        )
    }

    /**
     * 상점 요약 응답 DTO로 변환
     */
    fun toSummaryDto(userLocation: LocationDto? = null, nowTime: ZonedDateTime = ZonedDateTime.now()): StoreSummary {
        val distance = userLocation?.let {
            val userAddress = Address(
                roadAddress = RoadAddress("", "", null),
                legalAddress = LegalAddress("", "", null),
                adminAddress = AdminAddress(null),
                latitude = it.lat,
                longitude = it.lng
            )
            address.distanceTo(userAddress)
        }
        
        return StoreSummary(
            storeId = id,
            name = name,
            mainImageUrl = imageUrl,
            status = status,
            isAvailableForPickup = isAvailableForPickup(nowTime),
            pickupAvailableForTomorrow = pickupAvailableForTomorrow,
            distance = distance
        )
    }
}

/**
 * 매장 주소 정보 - 피드백 후 변경 가능성 높음
 */
class Address(
    // 도로명 주소 (필수)
    val roadAddress: RoadAddress,
    // 지번 주소 (도로명 주소와 함께 등록 시점에 입력됨)
    val legalAddress: LegalAddress,
    // 행정동 주소 (선택적, 나중에 입력 가능)
    val adminAddress: AdminAddress? = null,
    // 위도, 경도
    val latitude: Double,
    val longitude: Double
) {
    /**
     * 두 위치 사이의 거리 계산(km 단위, 좌표 기준 계산)
     */
    fun distanceTo(other: Address): Double {
        val earthRadius = 6371.0 // 지구 반지름 (km)
        
        val lat1Rad = Math.toRadians(this.latitude)
        val lat2Rad = Math.toRadians(other.latitude)
        val lon1Rad = Math.toRadians(this.longitude)
        val lon2Rad = Math.toRadians(other.longitude)
        
        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }
    
    /**
     * 특정 반경(km) 내에 위치하는지 확인
     */
    fun isWithinRadius(other: Address, radiusKm: Double): Boolean {
        return distanceTo(other) <= radiusKm
    }
}

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
    val buildingName: String? = null  // 건물명 (선택)
)

/**
 * 법정동 주소
 */
data class LegalAddress(
    val fullAddress: String,      // 전체 지번 주소
    val mainAddressNo: String,    // 본번
    val subAddressNo: String? = null  // 부번 (선택)
)

/**
 * 행정동 주소
 */
data class AdminAddress(
    val fullAddress: String?       // 전체 행정동 주소
)