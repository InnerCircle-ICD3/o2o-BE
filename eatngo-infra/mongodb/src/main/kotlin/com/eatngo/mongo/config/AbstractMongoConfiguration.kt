package com.eatngo.mongo.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter


abstract class AbstractMongoConfiguration: AbstractMongoClientConfiguration() {

    protected abstract fun getMongoConfig(): MongoConfig

    override fun getDatabaseName(): String {
        return this.getMongoConfig().database
    }

    override fun mongoClient(): MongoClient {
        val config = getMongoConfig()

        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(config.uri))
            .build()

        return MongoClients.create(settings)
    }

    override fun mongoTemplate(databaseFactory: MongoDatabaseFactory, converter: MappingMongoConverter): MongoTemplate {
        return super.mongoTemplate(databaseFactory, converter)
    }

}