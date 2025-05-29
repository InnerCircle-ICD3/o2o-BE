package com.eatngo.common.type

import com.eatngo.store.vo.LotNumberAddressVO
import com.eatngo.store.vo.RoadNameAddressVO
import com.eatngo.store.vo.ZipCodeVO

/**
 * 매장 위치 정보
 */
data class Address(
    val roadNameAddress: RoadNameAddressVO,     // 도로명 주소
    val lotNumberAddress: LotNumberAddressVO,   // 지번 주소
    val buildingName: String?,                  // 건물명
    val zipCode: ZipCodeVO,                     // 우편번호
    val region1DepthName: String?,              // 시도명
    val region2DepthName: String?,              // 시군구명
    val region3DepthName: String?,              // 상세주소
    val coordinate: CoordinateVO                // 위도, 경도
)