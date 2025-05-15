package com.eatngo.store.domain

import com.eatngo.store.constant.StoreEnum
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * 매장(가게) 도메인 모델
 */
class Store(
    val id: Long,
    val ownerId: String,
    val name: String,
    val description: String,
    val address: Address,
    val businessNumber: String,
    val contactNumber: String,
    val imageUrl: String?,
    val businessHours: List<BusinessHour>,
    val categories: List<String>,
    val status: StoreEnum.StoreStatus,
    val pickupStartTime: LocalTime, // 픽업 시작 시간 (필수)
    val pickupEndTime: LocalTime,   // 픽업 종료 시간 (필수)
    val pickupAvailableForTomorrow: Boolean = false, // 내일 픽업 가능 여부
    val postalCode: String?,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    var deletedAt: ZonedDateTime? = null,
) {
    /**
     * 영업시간 정보 반환 (정보 표시용)
     * 영업 시간은 단순히 정보성으로만 사용되며 매장의 활성화 여부에 영향을 주지 않음
     */
    fun getBusinessHoursInfo(dayOfWeek: DayOfWeek? = null): List<BusinessHour> {
        return if (dayOfWeek != null) {
            businessHours.filter { it.dayOfWeek == dayOfWeek }
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
        categories: List<String>? = null,
        status: StoreEnum.StoreStatus? = null,
        pickupStartTime: LocalTime? = null,
        pickupEndTime: LocalTime? = null,
        pickupAvailableForTomorrow: Boolean? = null,
        postalCode: String? = null
    ): Store {
        val updatedStore = Store(
            id = this.id,
            ownerId = this.ownerId,
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
            postalCode = postalCode ?: this.postalCode,
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
}

/**
 * 매장 주소 정보
 */
class Address(
    val roadAddress: RoadAddress,
    val lotAddress: LotAddress,
    val addressType: StoreEnum.AddressType,
    val postalCode: String?,
    val latitude: Double,
    val longitude: Double
) {
    /**
     * 두 위치 사이의 거리 계산(km 단위)
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
class BusinessHour(
    val dayOfWeek: DayOfWeek,
    val openTime: LocalTime,
    val closeTime: LocalTime
) {
    /**
     * 주어진 시간이 영업시간 내인지 확인
     */
    fun isWithinOpenHours(time: LocalTime): Boolean {
        // closeTime이 openTime보다 이전이면 자정을 넘어가는 경우 (예: 22:00 ~ 02:00)
        return if (closeTime.isBefore(openTime)) {
            time.isAfter(openTime) || time.isBefore(closeTime)
        } else {
            time.isAfter(openTime) && time.isBefore(closeTime)
        }
    }
}

/**
 * 도로명 주소
 */
class RoadAddress(
    val addressName: String,
    val zoneNo: String,
    val buildingName: String?
)

/**
 * 지번 주소
 */
class LotAddress(
    val addressName: String,
    val mainAddressNo: String,
    val subAddressNo: String?
)