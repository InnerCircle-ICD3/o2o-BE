package com.eatngo.mongo.config

import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Mongo DB 연결 정보를 구성하는 클래스
 */
@Primary
@Configuration
@ConfigurationProperties(prefix = "datastore.mongodb")
class MongoConfig: MongoProperties() {
}