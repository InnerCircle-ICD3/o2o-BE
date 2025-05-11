package com.eatngo.mongo.config.log

import com.eatngo.mongo.config.MongoConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "datastore.mongodb.log")
class LogMongoConfig: MongoConfig() {
}