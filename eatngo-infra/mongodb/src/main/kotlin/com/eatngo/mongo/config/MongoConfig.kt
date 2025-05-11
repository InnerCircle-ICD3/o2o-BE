package com.eatngo.mongo.config

import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Configuration

@Configuration
class MongoConfig: MongoProperties() {
}