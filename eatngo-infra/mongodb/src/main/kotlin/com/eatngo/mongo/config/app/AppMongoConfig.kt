package com.eatngo.mongo.config.app

import com.eatngo.mongo.config.MongoConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Primary
@Configuration
@ConfigurationProperties(prefix = "datastore.mongodb.app")
class AppMongoConfig: MongoConfig() {
}