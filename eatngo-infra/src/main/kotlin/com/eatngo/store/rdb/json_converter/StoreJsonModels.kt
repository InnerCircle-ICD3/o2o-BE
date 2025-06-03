package com.eatngo.store.rdb.json_converter

import com.eatngo.common.constant.StoreEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BusinessHourJson(
    val dayOfWeek: String,
    val openTime: String,
    val closeTime: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StoreCategoryJson(val value: List<StoreEnum.StoreCategory>)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FoodCategoryJson(val value: List<String>)