package com.eatngo.store.vo

import com.eatngo.common.constant.StoreEnum
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.text.isNotBlank

@JvmInline
value class StoreName(val value: String) {
    init {
        require(value.isNotBlank()) { "매장명은 비어있을 수 없습니다" }
        require(value.length in 1..50) { "매장명은 1~50자 사이여야 합니다" }
    }

    companion object {
        fun from(name: String): StoreName = StoreName(name)
    }
}

@JvmInline
value class BusinessNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "사업자등록번호는 비어있을 수 없습니다" }
        require(value.matches(Regex("^\\d{10}$"))) { "사업자등록번호는 10자리 숫자여야 합니다" }
    }

    companion object {
        fun from(number: String): BusinessNumber = BusinessNumber(number)
    }
}

@JvmInline
value class ContactNumber(val value: String) {
    init {
        require(value.matches(Regex("^\\d{2,3}-\\d{3,4}-\\d{4}$"))) { 
            "올바른 전화번호 형식이 아닙니다 (예: 02-123-4567)" 
        }
    }

    companion object {
        fun from(number: String): ContactNumber = ContactNumber(number)
    }
}

@JvmInline
value class ImageUrl(val value: String) {
    init {
        require(value.matches(Regex("^https?://.*"))) { "올바른 URL 형식이 아닙니다" }
    }

    companion object {
        fun from(url: String): ImageUrl = ImageUrl(url)
    }
}

@JvmInline
value class BusinessHour(val value: Triple<DayOfWeek, LocalTime, LocalTime>) {
    init {
        require(value.second.isBefore(value.third)) { "영업 종료 시간은 시작 시간보다 이후여야 합니다" }
    }

    val dayOfWeek: DayOfWeek get() = value.first
    val openTime: LocalTime get() = value.second
    val closeTime: LocalTime get() = value.third

    companion object {
        fun from(dayOfWeek: DayOfWeek, openTime: LocalTime, closeTime: LocalTime): BusinessHour =
            BusinessHour(Triple(dayOfWeek, openTime, closeTime))
    }
}

@JvmInline
value class PickUpInfo(val value: Triple<StoreEnum.PickupDay, LocalTime, LocalTime>) {
    init {
        requireNotNull(value.first) { "pickupDay는 null일 수 없습니다." }
        require(value.second.isBefore(value.third)) { "픽업 종료 시간은 시작 시간보다 이후여야 합니다" }
    }

    val pickupDay: StoreEnum.PickupDay get() = value.first
    val startTime: LocalTime get() = value.second
    val endTime: LocalTime get() = value.third

    companion object {
        fun from(pickupDay: StoreEnum.PickupDay, startTime: LocalTime, endTime: LocalTime): PickUpInfo =
            PickUpInfo(Triple(pickupDay, startTime, endTime))
    }
}

@JvmInline
value class StoreCategory(val value: String) {
    init {
        require(value.isNotBlank()) { "매장 카테고리는 비어있을 수 없습니다" }
        require(value.length in 1..10) { "매장 카테고리는 1~10자 사이여야 합니다" }
    }

    companion object {
        fun from(category: String): StoreCategory = StoreCategory(category)
    }
}

@JvmInline
value class FoodCategory(val value: String) {
    init {
        require(value.isNotBlank()) { "음식 카테고리는 비어있을 수 없습니다" }
    }

    companion object {
        fun from(category: String): FoodCategory = FoodCategory(category)
    }
}

@JvmInline
value class Coordinate(val value: Pair<Double, Double>) {
    init {
        require(value.first in -90.0..90.0) { "위도는 -90에서 90 사이여야 합니다" }
        require(value.second in -180.0..180.0) { "경도는 -180에서 180 사이여야 합니다" }
    }

    val latitude: Double get() = value.first
    val longitude: Double get() = value.second

    companion object {
        fun from(latitude: Double, longitude: Double): Coordinate = 
            Coordinate(Pair(latitude, longitude))
    }
}

@JvmInline
value class Description(val value: String) {
    init {
        require(value.length <= 500) { "설명은 500자를 초과할 수 없습니다" }
    }

    companion object {
        fun from(description: String?): Description? = description?.let { Description(it) }
    }
}

@JvmInline
value class FullAddressVO(val value: String) {
    init {
        require(value.isNotBlank()) { "도로명 주소는 비어있을 수 없습니다" }
    }

    companion object {
        fun from(address: String?): FullAddressVO = FullAddressVO(address ?: "")
    }
}

@JvmInline
value class ZoneNoVO(val value: String) {
    init {
        require(value.isNotBlank()) { "우편번호는 비어있을 수 없습니다" }
    }

    companion object {
        fun from(zoneNo: String?): ZoneNoVO = ZoneNoVO(zoneNo ?: "")
    }
}
