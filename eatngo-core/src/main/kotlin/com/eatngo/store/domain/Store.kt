package com.eatngo.store.domain

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.StoreException
import com.eatngo.common.type.Address
import com.eatngo.common.type.CoordinateVO
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
                    roadNameAddress = RoadNameAddressVO.from(dto.address.roadNameAddress),
                    lotNumberAddress = LotNumberAddressVO.from(dto.address.lotNumberAddress),
                    buildingName = dto.address.buildingName,
                    zipCode = ZipCodeVO.from(dto.address.zipCode),
                    region1DepthName = dto.address.region1DepthName,
                    region2DepthName = dto.address.region2DepthName,
                    region3DepthName = dto.address.region3DepthName,
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
                    roadNameAddress = RoadNameAddressVO.from(request.address.roadNameAddress),
                    lotNumberAddress = LotNumberAddressVO.from(request.address.lotNumberAddress),
                    buildingName = request.address.buildingName,
                    zipCode = ZipCodeVO.from(request.address.zipCode),
                    region1DepthName = request.address.region1DepthName,
                    region2DepthName = request.address.region2DepthName,
                    region3DepthName = request.address.region3DepthName,
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
        if (storeOwnerId != this.storeOwnerId) {
            throw StoreException.Forbidden(storeOwnerId)
        }
    }

    /**
     * 매장 정보 업데이트 (점주용)
     */
    fun update(request: StoreUpdateDto) {
        request.name?.let { this.name = StoreNameVO.from(it) }
        request.description?.let { this.description = DescriptionVO.from(it) }
        request.address?.let { addr ->
            this.address = Address(
                roadNameAddress = RoadNameAddressVO.from(addr.roadNameAddress),
                lotNumberAddress = LotNumberAddressVO.from(addr.lotNumberAddress),
                buildingName = addr.buildingName,
                zipCode = ZipCodeVO.from(addr.zipCode),
                region1DepthName = addr.region1DepthName,
                region2DepthName = addr.region2DepthName,
                region3DepthName = addr.region3DepthName,
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
}

/**
 * 매장의 카테고리 정보(분류와 사용자 입력 카테고리)
 */
data class StoreCategoryInfo(
    val storeCategory: List<StoreCategoryVO>,    // 매장의 카테고리(ex. 빵, 카페, 분식 ...)
    val foodCategory: List<FoodCategoryVO>?     // 음식의 카테고리(ex. 햄버거, 소금빵, 모카빵 ...)
)