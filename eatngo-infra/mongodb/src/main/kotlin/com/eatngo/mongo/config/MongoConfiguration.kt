package com.eatngo.mongo.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * MongoProperties 설정을 주입받아 MongoClient 및 MongoRepository를 구성하는 메인 MongoDB 설정 클래스
 */
@Configuration
@EnableMongoRepositories(basePackages = ["com.eatngo.mongo.repository"])
class MongoConfiguration(
    private val mongoConfig: MongoConfig
): AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        return mongoConfig.database
    }

    override fun mongoClient(): MongoClient {
        return MongoClients.create(
            MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(mongoConfig.uri))
            .build()
        )
    }
}