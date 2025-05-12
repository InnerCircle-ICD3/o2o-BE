package com.eatngo.mongo.config.log

import com.eatngo.mongo.config.MongoConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * LOG Mongo Config 클래스
 */
@Configuration
@ConfigurationProperties(prefix = "datastore.mongodb.log")
class LogMongoConfig: MongoConfig() {
}