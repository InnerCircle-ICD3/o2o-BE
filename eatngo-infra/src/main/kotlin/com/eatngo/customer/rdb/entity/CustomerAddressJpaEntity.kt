package com.eatngo.customer.rdb.entity

import com.eatngo.common.BaseJpaEntity
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
data class CustomerAddressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    val customer: CustomerJpaEntity,
    val roadNameAddress: RoadNameAddressVO,
    val lotNumberAddress: LotNumberAddressVO,
    val buildingName: String?,
    val zipCode: ZipCodeVO,
    val region1DepthName: String?,
    val region2DepthName: String?,
    val region3DepthName: String?,
    val coordinate: CoordinateVO,
    val customerAddressType: CustomerAddressType,
    val description: String?,
) : BaseJpaEntity() {
    companion object {
        fun of(
            customer: CustomerJpaEntity,
            customerAddress: CustomerAddress
        ): CustomerAddressJpaEntity =
            CustomerAddressJpaEntity(
                customer = customer,
                roadNameAddress = customerAddress.address.roadNameAddress,
                lotNumberAddress = customerAddress.address.lotNumberAddress,
                buildingName = customerAddress.address.buildingName,
                zipCode = customerAddress.address.zipCode,
                region1DepthName = customerAddress.address.region1DepthName,
                region2DepthName = customerAddress.address.region2DepthName,
                region3DepthName = customerAddress.address.region3DepthName,
                coordinate = customerAddress.address.coordinate,
                customerAddressType = customerAddress.customerAddressType,
                description = customerAddress.description
            )

        fun toCustomerAddress(customerAddressJpaEntity: CustomerAddressJpaEntity): CustomerAddress {
            return CustomerAddress(
                addressId = customerAddressJpaEntity.id,
                customerId = customerAddressJpaEntity.customer.id,
                address = com.eatngo.common.type.Address(
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
