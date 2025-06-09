package com.eatngo.store.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.type.Address
import com.eatngo.common.type.CoordinateVO
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.store.domain.Store
import com.eatngo.store.domain.StoreCategoryInfo
import com.eatngo.store.rdb.json_converter.*
import com.eatngo.store.vo.*
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Filter(name = DELETED_FILTER)
@Table(
    name = "store",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_store_business_number",
            columnNames = ["business_number"]
        )
    ]
)
class StoreJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val storeOwnerId: Long,

    @Column(nullable = false)
    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String?,

//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(columnDefinition = "jsonb", nullable = false)
//    @Convert(converter = AddressJsonConverter::class)
//    val address: AddressJson,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "address_id", nullable = false)
    val address: AddressJpaEntity,

    @Column(name = "business_number", length = 10, nullable = false)
    val businessNumber: String,

    @Column
    val contactNumber: String?,

    @Column(columnDefinition = "TEXT")
    val imageUrl: String?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Convert(converter = BusinessHoursJsonConverter::class)
    val businessHours: List<BusinessHourJson>,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Convert(converter = StoreCategoryJsonConverter::class)
    val storeCategory: StoreCategoryJson,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Convert(converter = FoodCategoryJsonConverter::class)
    val foodCategory: FoodCategoryJson,

    @Enumerated(EnumType.STRING)
    val status: StoreEnum.StoreStatus,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val pickUpDay: StoreEnum.PickupDay
) : BaseJpaEntity() {
    companion object {
        fun from(store: Store): StoreJpaEntity {
            val storeJpaEntity = StoreJpaEntity(
                id = store.id,
                storeOwnerId = store.storeOwnerId,
                name = store.name.value,
                description = store.description?.value,
                address = AddressJpaEntity(
                    id = store.addressId,
                    roadNameAddress = store.address.roadNameAddress?.value,
                    lotNumberAddress = store.address.lotNumberAddress.value,
                    buildingName = store.address.buildingName,
                    zipCode = store.address.zipCode.value,
                    region1DepthName = store.address.region1DepthName,
                    region2DepthName = store.address.region2DepthName,
                    region3DepthName = store.address.region3DepthName,
                    latitude = store.address.coordinate.latitude,
                    longitude = store.address.coordinate.longitude
                ),
                businessNumber = store.businessNumber.value,
                contactNumber = store.contactNumber?.value,
                imageUrl = store.imageUrl,
                businessHours = store.businessHours?.map {
                    BusinessHourJson(
                        dayOfWeek = it.dayOfWeek.name,
                        openTime = it.openTime.toString(),
                        closeTime = it.closeTime.toString()
                    )
                } ?: emptyList(),
                storeCategory = StoreCategoryJson(store.storeCategoryInfo.storeCategory.map { it.value }),
                foodCategory = FoodCategoryJson(store.storeCategoryInfo.foodCategory?.map { it.value } ?: emptyList()),
                status = store.status,
                pickUpDay = store.pickUpDay.pickUpDay
            )

            return storeJpaEntity
        }

        fun toStore(storeJpaEntity: StoreJpaEntity) = with(storeJpaEntity) {
            Store(
                id = id,
                storeOwnerId = storeOwnerId,
                name = StoreNameVO.from(name),
                description = description?.let { DescriptionVO.from(it) },
                address = Address(
                    roadNameAddress = RoadNameAddressVO.from(address.roadNameAddress),
                    lotNumberAddress = LotNumberAddressVO.from(address.lotNumberAddress),
                    buildingName = address.buildingName,
                    zipCode = ZipCodeVO.from(address.zipCode),
                    region1DepthName = address.region1DepthName,
                    region2DepthName = address.region2DepthName,
                    region3DepthName = address.region3DepthName,
                    coordinate = CoordinateVO.from(address.latitude, address.longitude)
                ),
                addressId = address.id,
                businessNumber = BusinessNumberVO.from(businessNumber),
                contactNumber = contactNumber?.let { ContactNumberVO.from(it) },
                imageUrl = imageUrl,
                businessHours = businessHours.map {
                    BusinessHourVO.from(
                        dayOfWeek = java.time.DayOfWeek.valueOf(it.dayOfWeek),
                        openTime = java.time.LocalTime.parse(it.openTime),
                        closeTime = java.time.LocalTime.parse(it.closeTime)
                    )
                },
                storeCategoryInfo = StoreCategoryInfo(
                    storeCategory = storeCategory.value.map { StoreCategoryVO(it) },
                    foodCategory = foodCategory.value.map { FoodCategoryVO.from(it) }
                ),
                status = status,
                pickUpDay = PickUpDayVO.from(pickUpDay),
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }
}
