package com.eatngo.customer.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.common.type.Address
import com.eatngo.common.type.CoordinateVO
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.domain.CustomerAddressType
import com.eatngo.store.vo.LotNumberAddressVO
import com.eatngo.store.vo.RoadNameAddressVO
import com.eatngo.store.vo.ZipCodeVO
import jakarta.persistence.*
import org.hibernate.annotations.Filter


@Filter(name = DELETED_FILTER)
@Entity
@Table(name = "customer_addresses")
data class CustomerAddressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    val customer: CustomerJpaEntity,
    val radiusInKilometers: Double = 0.0,
    @Column(nullable = true)
    val roadNameAddress: RoadNameAddressVO? = null,
    val lotNumberAddress: LotNumberAddressVO,
    val buildingName: String?,
    val zipCode: ZipCodeVO,
    val region1DepthName: String?,
    val region2DepthName: String?,
    val region3DepthName: String?,
    val coordinate: CoordinateVO,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val customerAddressType: CustomerAddressType,
    val description: String?,
) : BaseJpaEntity() {
    companion object {
        fun of(
            customer: CustomerJpaEntity,
            customerAddress: CustomerAddress
        ): CustomerAddressJpaEntity =
            with(customerAddress) {
                CustomerAddressJpaEntity(
                    customer = customer,
                    radiusInKilometers = radiusInKilometers,
                    roadNameAddress = address.roadNameAddress,
                    lotNumberAddress = address.lotNumberAddress,
                    buildingName = address.buildingName,
                    zipCode = address.zipCode,
                    region1DepthName = address.region1DepthName,
                    region2DepthName = address.region2DepthName,
                    region3DepthName = address.region3DepthName,
                    coordinate = address.coordinate,
                    customerAddressType = customerAddressType,
                    description = description
                )
            }


        fun toCustomerAddress(customerAddressJpaEntity: CustomerAddressJpaEntity): CustomerAddress {
            return CustomerAddress(
                id = customerAddressJpaEntity.id,
                customerId = customerAddressJpaEntity.customer.id,
                radiusInKilometers = customerAddressJpaEntity.radiusInKilometers,
                address = Address(
                    roadNameAddress = customerAddressJpaEntity.roadNameAddress,
                    lotNumberAddress = customerAddressJpaEntity.lotNumberAddress,
                    buildingName = customerAddressJpaEntity.buildingName,
                    zipCode = customerAddressJpaEntity.zipCode,
                    region1DepthName = customerAddressJpaEntity.region1DepthName,
                    region2DepthName = customerAddressJpaEntity.region2DepthName,
                    region3DepthName = customerAddressJpaEntity.region3DepthName,
                    coordinate = customerAddressJpaEntity.coordinate
                ),
                customerAddressType = customerAddressJpaEntity.customerAddressType,
                description = customerAddressJpaEntity.description,
            )
        }
    }

}
