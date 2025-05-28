package com.eatngo.store.domain

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.vo.*
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 매장(가게) 도메인 모델
 */
class Store(
    val id: Long,                       // 매장의 고유 id
    val storeOwnerId: Long,             // 해당 매장을 소유한 점주계정 id(매장:계정 1:1)
    var name: StoreNameVO,                // 매장명
    var description: DescriptionVO?,           // 매장 설명
    var address: Address,               // 매장 주소
    val businessNumber: BusinessNumberVO, // 사업자등록 번호
    var contactNumber: ContactNumberVO?,  // 매장 or 점주 전화번호
    var imageUrl: ImageUrlVO?,            // 대표 이미지 url(카드뷰에 보이는 이미지)
    var businessHours: List<BusinessHourVO>?, // 영업시간(픽업시간과는 다름, 단순 정보제공용 및 확장성 고려해 추가)
    var storeCategoryInfo: StoreCategoryInfo, // 매장의 카테고리 정보들
    var status: StoreEnum.StoreStatus = StoreEnum.StoreStatus.PENDING, // 매장 상태(기본: 승인대기중)
    var pickUpInfo: PickUpInfoVO,         // 픽업과 관련된 정보(픽업시간 from to, 오늘/내일 픽업)
    val reviewInfo: ReviewInfoVO,         // 리뷰와 관련된 정보(평균 별점, 리뷰 개수)
    val createdAt: LocalDateTime,       // 생성일
    var updatedAt: LocalDateTime,       // 수정일
    var deletedAt: LocalDateTime? = null, // 삭제일(softDel용, 삭제 시간이 존재하면 softDel)
) {
    companion object {
        fun from(dto: StoreDto): Store {
            return Store(
                id = dto.storeId,
                storeOwnerId = dto.storeOwnerId,
                name = StoreNameVO.from(dto.name),
                description = DescriptionVO.from(dto.description),
                address = Address(
                    roadAddress = RoadAddressVO.from(
                        fullAddress = dto.address.roadAddress.fullAddress,
                        zoneNo = dto.address.roadAddress.zoneNo
                    ),
                    legalAddress = dto.address.legalAddress?.fullAddress?.let { LegalAddress(it) },
                    adminAddress = dto.address.adminAddress?.fullAddress?.let { AdminAddress(it) },
                    coordinate = CoordinateVO.from(
                        dto.address.coordinate.latitude ?: 0.0,
                        dto.address.coordinate.longitude ?: 0.0
                    )
                ),
                businessNumber = BusinessNumberVO.from(dto.businessNumber),
                contactNumber = dto.contactNumber?.let { ContactNumberVO.from(it) },
                imageUrl = dto.imageUrl?.let { ImageUrlVO.from(it) },
                businessHours = BusinessHourVO.fromList(dto.businessHours),
                storeCategoryInfo = StoreCategoryInfo(
                    storeCategory = dto.storeCategoryInfo.storeCategory?.map { StoreCategoryVO.from(it) }
                        ?: emptyList(),
                    foodCategory = dto.storeCategoryInfo.foodCategory?.map { FoodCategoryVO.from(it) }
                ),
                status = dto.status,
                pickUpInfo = PickUpInfoVO.from(
                    dto.pickUpInfo.pickupDay!!,
                    dto.pickUpInfo.pickupStartTime!!,
                    dto.pickUpInfo.pickupEndTime!!
                ),
                reviewInfo = ReviewInfoVO.from(
                    dto.reviewInfo.ratingAverage,
                    dto.reviewInfo.ratingCount
                ),
                createdAt = dto.createdAt,
                updatedAt = dto.updatedAt,
                deletedAt = null
            )
        }

        /**
         * 매장 생성 (점주용)
         */
        fun create(request: StoreCreateDto): Store {
            return Store(
                id = 0L,
                storeOwnerId = request.storeOwnerId,
                name = StoreNameVO.from(request.name),
                description = DescriptionVO.from(request.description),
                address = Address(
                    roadAddress = RoadAddressVO.from(
                        request.address.roadAddress.fullAddress,
                        request.address.roadAddress.zoneNo
                    ),
                    legalAddress = request.address.legalAddress?.fullAddress?.let { LegalAddress(it) },
                    adminAddress = request.address.adminAddress?.fullAddress?.let { AdminAddress(it) },
                    coordinate = CoordinateVO.from(
                        request.address.coordinate.latitude ?: 0.0,
                        request.address.coordinate.longitude ?: 0.0
                    )
                ),
                businessNumber = BusinessNumberVO.from(request.businessNumber),
                contactNumber = request.contactNumber?.let { ContactNumberVO.from(it) },
                imageUrl = request.imageUrl?.let { ImageUrlVO.from(it) },
                businessHours = request.businessHours?.map {
                    BusinessHourVO.from(it.dayOfWeek, it.openTime, it.closeTime)
                } ?: emptyList(),
                storeCategoryInfo = StoreCategoryInfo(
                    storeCategory = request.storeCategoryInfo.storeCategory?.map { StoreCategoryVO.from(it) }
                        ?: emptyList(),
                    foodCategory = request.storeCategoryInfo.foodCategory?.map { FoodCategoryVO.from(it) }
                ),
                status = StoreEnum.StoreStatus.PENDING,
                pickUpInfo = PickUpInfoVO.from(
                    request.pickUpInfo.pickupDay!!,
                    request.pickUpInfo.pickupStartTime!!,
                    request.pickUpInfo.pickupEndTime!!
                ),
                reviewInfo = ReviewInfoVO.from(0.0, 0),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                deletedAt = null
            )
        }
    }


    /**
     * 점주 권한 확인 메서드
     */
    fun requireOwner(storeOwnerId: Long) {
        require(storeOwnerId == this.storeOwnerId) { "점주(ID: ${storeOwnerId})는 해당 매장(ID: ${this.id})의 작업을 수행할 수 없습니다." }
    }

    /**
     * 매장 정보 업데이트 (점주용)
     */
    fun update(request: StoreUpdateDto) {
        request.name?.let { this.name = StoreNameVO.from(it) }
        request.description?.let { this.description = DescriptionVO.from(it) }
        request.address?.let { addr ->
            this.address = Address(
                roadAddress = RoadAddressVO.from(
                    fullAddress = addr.roadAddress.fullAddress,
                    zoneNo = addr.roadAddress.zoneNo
                ),
                legalAddress = addr.legalAddress?.fullAddress?.let { LegalAddress(it) },
                adminAddress = addr.adminAddress?.fullAddress?.let { AdminAddress(it) },
                coordinate = CoordinateVO.from(
                    addr.coordinate.latitude ?: 0.0,
                    addr.coordinate.longitude ?: 0.0
                )
            )
        }
        request.contactNumber?.let { this.contactNumber = ContactNumberVO.from(it) }
        request.mainImageUrl?.let { this.imageUrl = ImageUrlVO.from(it) }
        request.businessHours?.let {
            this.businessHours = it.map { hour ->
                BusinessHourVO.from(hour.dayOfWeek, hour.openTime, hour.closeTime)
            }
        }
        request.storeCategoryInfo?.let { info ->
            this.storeCategoryInfo = StoreCategoryInfo(
                storeCategory = info.storeCategory?.map { StoreCategoryVO.from(it) } ?: emptyList(),
                foodCategory = info.foodCategory?.map { FoodCategoryVO.from(it) }
            )
        }
        request.pickUpInfo?.let { info ->
            this.pickUpInfo = PickUpInfoVO.from(
                info.pickupDay!!,
                info.pickupStartTime!!,
                info.pickupEndTime!!
            )
        }
        this.updatedAt = LocalDateTime.now()
    }

    /**
     *  상태 전이 메서드 (점주용)
     */
    fun toOpen() {
        status = status.open()
        updatedAt = LocalDateTime.now()
    }

    fun toClose() {
        status = status.close()
        updatedAt = LocalDateTime.now()
    }

    fun toPending() {
        status = status.pending()
        updatedAt = LocalDateTime.now()
    }

    /**
     * 활성화된 픽업 시간이 정확히 되었는지(=알림 시점) 반환
     */
    fun isPickupTime(): Boolean {
        val currentTime = LocalDateTime.now().toLocalTime()
        val startWindow = pickUpInfo.pickupStartTime.minusMinutes(1)
        val endWindow = pickUpInfo.pickupStartTime.plusMinutes(1)
        return currentTime.isAfter(startWindow) && currentTime.isBefore(endWindow)
    }

    /**
     * 시스템 상에서 매장 상태를 전환
     * - hasStock: 재고가 있는지 여부
     */
    fun updateStoreStatus(hasStock: Boolean) {
        val now = LocalDateTime.now()
        val isAfterMidnight = pickUpInfo.pickupEndTime.isBefore(pickUpInfo.pickupStartTime)

        val isInPickupTime = if (isAfterMidnight) {
            now.toLocalTime() >= pickUpInfo.pickupStartTime || now.toLocalTime() <= pickUpInfo.pickupEndTime
        } else {
            now.toLocalTime() >= pickUpInfo.pickupStartTime && now.toLocalTime() <= pickUpInfo.pickupEndTime
        }

        status = when {
            !hasStock -> StoreEnum.StoreStatus.CLOSED
            isInPickupTime -> status.open()
            else -> status.close()
        }

        this.updatedAt = now
    }

    /**
     * 픽업 정보만 업데이트
     */
    fun updatePickupInfo(
        pickupDay: StoreEnum.PickupDay?,
        startTime: LocalTime?,
        endTime: LocalTime?
    ) {
        this.pickUpInfo = PickUpInfoVO.from(
            pickupDay!!,
            startTime!!,
            endTime!!
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
    val roadAddress: RoadAddressVO,           // 도로명 주소 (필수)
    val legalAddress: LegalAddress? = null, // 법정동 주소
    val adminAddress: AdminAddress? = null, // 행정동 주소
    val coordinate: CoordinateVO              // 위도, 경도
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
 * 매장의 카테고리 정보(분류와 사용자 입력 카테고리)
 */
data class StoreCategoryInfo(
    val storeCategory: List<StoreCategoryVO>,    // 매장의 카테고리(ex. 빵, 카페, 분식 ...)
    val foodCategory: List<FoodCategoryVO>?     // 음식의 카테고리(ex. 햄버거, 소금빵, 모카빵 ...)
)