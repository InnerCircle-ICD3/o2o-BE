package com.eatngo.store.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.converter.JsonAttributeConverter
import com.eatngo.common.type.Address
import com.eatngo.common.type.CoordinateVO
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.store.domain.*
import com.eatngo.store.vo.*
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.*
import org.hibernate.annotations.Filter

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

//    @Column(columnDefinition = "jsonb", nullable = false) TODO: 나중에 진짜 db 환경에서는 컬럼 Jsonb로 변경 필요
    @Column(columnDefinition = "TEXT", nullable = false)
    @Convert(converter = AddressJsonConverter::class)
    val address: AddressJson,

    @Column(name = "business_number", length = 10, nullable = false)
    val businessNumber: String,

    @Column
    val contactNumber: String?,

    @Column(columnDefinition = "TEXT")
    val imageUrl: String?,

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @Filter(name = DELETED_FILTER)
    val businessHours: MutableList<BusinessHourJpaEntity> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "store_categories", joinColumns = [JoinColumn(name = "store_id")])
    val storeCategories: MutableList<StoreEnum.StoreCategory> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "store_food_categories", joinColumns = [JoinColumn(name = "store_id")])
    val foodCategories: MutableList<String> = mutableListOf(),

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
                address = AddressJson(
                    roadNameAddress = store.address.roadNameAddress.value,
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
                imageUrl = store.imageUrl?.value,
                storeCategories = store.storeCategoryInfo.storeCategory.map { it.value }.toMutableList(),
                foodCategories = store.storeCategoryInfo.foodCategory?.map { it.value }?.toMutableList() ?: mutableListOf(),
                status = store.status,
                pickUpDay = store.pickUpDay.pickUpDay
            )

            store.businessHours?.forEach {
                storeJpaEntity.businessHours.add(BusinessHourJpaEntity.of(it, storeJpaEntity))
            }

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
                businessNumber = BusinessNumberVO.from(businessNumber),
                contactNumber = contactNumber?.let { ContactNumberVO.from(it) },
                imageUrl = imageUrl?.let { ImageUrlVO.from(it) },
                businessHours = businessHours.map { BusinessHourJpaEntity.toVO(it) },
                storeCategoryInfo = StoreCategoryInfo(
                    storeCategory = storeCategories.map { StoreCategoryVO(it) },
                    foodCategory = foodCategories.map { FoodCategoryVO.from(it) }
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

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AddressJson(
    val roadNameAddress: String,
    val lotNumberAddress: String,
    val buildingName: String? = null,
    val zipCode: String,
    val region1DepthName: String? = null,
    val region2DepthName: String? = null,
    val region3DepthName: String? = null,
    val latitude: Double,
    val longitude: Double
)

@Converter(autoApply = false)
class AddressJsonConverter : JsonAttributeConverter<AddressJson>(AddressJson::class.java)