package com.eatngo.store.vo

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.dto.BusinessHourDto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.time.DayOfWeek
import java.time.LocalTime

@JvmInline
value class StoreNameVO(val value: String) {
    init {
        require(value.isNotBlank()) { "매장명은 비어있을 수 없습니다" }
        require(value.length in 1..50) { "매장명은 1~50자 사이여야 합니다" }
    }

    companion object {
        fun from(name: String): StoreNameVO = StoreNameVO(name)
    }
}

@JvmInline
value class BusinessNumberVO(val value: String) {
    init {
        require(value.length == 10) { "사업자등록번호는 10자리여야 합니다" }
        require(value.all { it.isDigit() }) { "사업자등록번호는 숫자만 포함해야 합니다" }
    }

    companion object {
        fun from(number: String): BusinessNumberVO = BusinessNumberVO(number)
    }
}

@JvmInline
value class ContactNumberVO(val value: String) {
    init {
        require(value.matches(Regex("^\\d{2,3}-\\d{3,4}-\\d{4}$"))) {
            "올바른 전화번호 형식이 아닙니다 (예: 02-123-4567)"
        }
    }

    companion object {
        fun from(number: String): ContactNumberVO = ContactNumberVO(number)
    }
}

@JvmInline
value class BusinessHourVO(val value: Triple<DayOfWeek, LocalTime, LocalTime>) {
    init {
        require(value.second.isBefore(value.third)) { "영업 종료 시간은 시작 시간보다 이후여야 합니다" }
    }

    val dayOfWeek: DayOfWeek get() = value.first
    val openTime: LocalTime get() = value.second
    val closeTime: LocalTime get() = value.third

    companion object {
        fun from(dayOfWeek: DayOfWeek, openTime: LocalTime, closeTime: LocalTime): BusinessHourVO =
            BusinessHourVO(Triple(dayOfWeek, openTime, closeTime))

        fun fromList(list: List<BusinessHourDto>?): List<BusinessHourVO> =
            list?.map { from(it.dayOfWeek, it.openTime, it.closeTime) } ?: emptyList()
    }
}

@JvmInline
value class PickUpDayVO(val pickUpDay: StoreEnum.PickupDay) {
    companion object {
        fun from(day: String?): PickUpDayVO {
            require(!day.isNullOrBlank()) { "pickUpDay는 null이거나 빈 값일 수 없습니다." }
            val upperDay = day.uppercase()
            val matched = StoreEnum.PickupDay.entries.find { it.name == upperDay }
            require(matched != null) {
                "pickUpDay는 ${StoreEnum.PickupDay.entries.joinToString(", ")} 중 하나여야 합니다. (입력값: $day)"
            }
            return PickUpDayVO(matched)
        }

        fun from(pickUpDay: StoreEnum.PickupDay): PickUpDayVO = PickUpDayVO(pickUpDay)
    }
}

@JvmInline
value class StoreCategoryVO(val value: StoreEnum.StoreCategory) {
    companion object {
        fun from(category: String): StoreCategoryVO =
            StoreCategoryVO(StoreEnum.StoreCategory.fromString(category)
                ?: throw IllegalArgumentException("존재하지 않는 카테고리입니다: $category"))
    }

    override fun toString(): String = value.category //한글명
}

@JvmInline
value class FoodCategoryVO(val value: String) {
    init {
        require(value.isNotBlank()) { "음식 카테고리는 비어있을 수 없습니다" }
    }

    companion object {
        fun from(category: String): FoodCategoryVO = FoodCategoryVO(category)
    }
}

@JvmInline
value class DescriptionVO @JsonCreator constructor(
    @get:JsonValue val value: String
) {
    init {
        require(value.length <= 500) { "설명은 500자를 초과할 수 없습니다" }
    }

    companion object {
        fun from(description: String?): DescriptionVO? = description?.let { DescriptionVO(it) }
    }
}

@JvmInline
value class RoadNameAddressVO @JsonCreator constructor(
    @get:JsonValue val value: String
) {
    init {
        require(value.isNotBlank()) { "도로명 주소는 비어있을 수 없습니다" }
    }

    companion object {
        fun from(roadNameAddress: String?): RoadNameAddressVO {
            return RoadNameAddressVO(requireNotNull(roadNameAddress) { "도로명 주소는 null일 수 없습니다" })
        }
    }
}

@JvmInline
value class LotNumberAddressVO @JsonCreator constructor(
    @get:JsonValue val value: String
) {
    init {
        require(value.isNotBlank()) { "지번 주소는 비어있을 수 없습니다" }
    }

    companion object {
        fun from(lotNumberAddress: String?): LotNumberAddressVO {
            return LotNumberAddressVO(requireNotNull(lotNumberAddress) { "지번 주소는 null일 수 없습니다" })
        }
    }
}

@JvmInline
value class ZipCodeVO @JsonCreator constructor(
    @get:JsonValue val value: String
) {
    init {
        require(value.isNotBlank()) { "우편번호는 비어있을 수 없습니다" }
        require(value.length == 5) { "우편번호는 5자리여야 합니다" }
        require(value.all { it.isDigit() }) { "우편번호는 숫자여야 합니다" }
    }

    companion object {
        fun from(zipCode: String?): ZipCodeVO {
            requireNotNull(zipCode) { "우편번호는 null일 수 없습니다" }
            return ZipCodeVO(zipCode)
        }
    }
}