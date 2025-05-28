package com.eatngo.store.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.common.constant.StoreEnum
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.store.domain.*
import com.eatngo.store.vo.*
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import java.time.LocalTime

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

    @Column(nullable = false)
    val roadNameAddress: String,

    @Column(nullable = false)
    val lotNumberAddress: String,

    @Column(nullable = false)
    val zipCode: String,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(name = "business_number", nullable = false)
    val businessNumber: Long,

    @Column
    val contactNumber: String?,

    @Column(columnDefinition = "TEXT")
    val imageUrl: String?,

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @Filter(name = DELETED_FILTER)
    val businessHours: MutableList<BusinessHourJpaEntity> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "store_categories", joinColumns = [JoinColumn(name = "store_id")])
    val storeCategories: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "store_food_categories", joinColumns = [JoinColumn(name = "store_id")])
    val foodCategories: MutableList<String> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    val status: StoreEnum.StoreStatus,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val pickupDay: StoreEnum.PickupDay,

    @Column(nullable = false)
    val pickupStartTime: LocalTime,

    @Column(nullable = false)
    val pickupEndTime: LocalTime
) : BaseJpaEntity() {
    companion object {
        fun from(store: Store): StoreJpaEntity {
            val storeJpaEntity = StoreJpaEntity(
                id = store.id,
                storeOwnerId = store.storeOwnerId,
                name = store.name.value,
                description = store.description?.value,
                roadNameAddress = store.address.roadNameAddress.value,
                lotNumberAddress = store.address.lotNumberAddress.value,
                zipCode = store.address.zipCode.value,
                latitude = store.address.coordinate.latitude,
                longitude = store.address.coordinate.longitude,
                businessNumber = store.businessNumber.value,
                contactNumber = store.contactNumber?.value,
                imageUrl = store.imageUrl?.value,
                storeCategories = store.storeCategoryInfo.storeCategory.map { it.value }.toMutableList(),
                foodCategories = store.storeCategoryInfo.foodCategory?.map { it.value }?.toMutableList() ?: mutableListOf(),
                status = store.status,
                pickupDay = store.pickUpInfo.pickupDay,
                pickupStartTime = store.pickUpInfo.pickupStartTime,
                pickupEndTime = store.pickUpInfo.pickupEndTime
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
                    roadNameAddress = RoadNameAddressVO.from(roadNameAddress),
                    lotNumberAddress = LotNumberAddressVO.from(lotNumberAddress),
                    zipCode = ZipCodeVO.from(zipCode),
                    coordinate = CoordinateVO.from(latitude, longitude)
                ),
                businessNumber = BusinessNumberVO.from(businessNumber),
                contactNumber = contactNumber?.let { ContactNumberVO.from(it) },
                imageUrl = imageUrl?.let { ImageUrlVO.from(it) },
                businessHours = businessHours.map { BusinessHourJpaEntity.toVO(it) },
                storeCategoryInfo = StoreCategoryInfo(
                    storeCategory = storeCategories.map { StoreCategoryVO.from(it) },
                    foodCategory = foodCategories.map { FoodCategoryVO.from(it) }
                ),
                status = status,
                pickUpInfo = PickUpInfoVO.from(pickupDay, pickupStartTime, pickupEndTime),
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }
} 