package com.eatngo.mongo.config

import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Configuration

/**
 * MongoProperties를 상속받아 Mongo 설정 값을 구성하는 설정 클래스
 */
@Configuration
class MongoConfig: MongoProperties() {
}