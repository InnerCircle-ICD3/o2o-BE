package com.eatngo.search.dto

// TODO : 공통적으로 사용하는 구조체 위치 조정 (core.eatngo.common.dto 에 두고 import 해서 쓸지, 도메인단위 안에서 정의할지)
// TODO : 우선은 Double로 정의했는데 String으로 받는게 나을지 논의 필요
data class Point (
    val lat: Double,
    val lng: Double,
)
