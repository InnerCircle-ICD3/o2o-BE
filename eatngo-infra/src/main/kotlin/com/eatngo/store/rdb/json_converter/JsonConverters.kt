package com.eatngo.store.rdb.json_converter

import com.eatngo.common.converter.JsonAttributeConverter
import com.eatngo.common.converter.JsonListAttributeConverter
import com.fasterxml.jackson.core.type.TypeReference
import jakarta.persistence.Converter

@Converter(autoApply = false)
class AddressJsonConverter : JsonAttributeConverter<AddressJson>(AddressJson::class.java)

@Converter(autoApply = false)
class BusinessHoursJsonConverter : JsonListAttributeConverter<List<BusinessHourJson>>(object : TypeReference<List<BusinessHourJson>>() {})

@Converter(autoApply = false)
class StoreCategoryJsonConverter : JsonAttributeConverter<StoreCategoryJson>(StoreCategoryJson::class.java)

@Converter(autoApply = false)
class FoodCategoryJsonConverter : JsonAttributeConverter<FoodCategoryJson>(FoodCategoryJson::class.java)
