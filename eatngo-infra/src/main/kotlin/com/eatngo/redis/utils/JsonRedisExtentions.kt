package com.eatngo.redis.utils

import com.fasterxml.jackson.databind.ObjectMapper

inline fun <reified T> ObjectMapper.readValueFromJson(json: String): T = this.readValue(json, T::class.java)

inline fun <reified T> ObjectMapper.writeValueToJson(value: T): String = this.writeValueAsString(value)
