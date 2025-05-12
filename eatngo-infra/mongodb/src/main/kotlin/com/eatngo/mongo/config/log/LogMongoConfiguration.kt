package com.eatngo.mongo.config.log

import com.eatngo.mongo.config.AbstractMongoConfiguration
import com.eatngo.mongo.config.MongoConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * LOG Mongo 설정을 정의하고 MongoDB 빈을 등록하는 설정 클래스
 */
@Configuration
@EnableMongoRepositories(basePackages = ["com.eatngo.mongo.repository.log"])
class LogMongoConfiguration: AbstractMongoConfiguration() {
    private var logMongoConfig: LogMongoConfig = LogMongoConfig()

    public override fun getMongoConfig(): MongoConfig {
        return logMongoConfig
    }

    override fun getDatabaseName(): String {
        return logMongoConfig.database
    }

    @Bean(name = ["logMongoDbFactory"])
    override fun mongoDbFactory(): MongoDatabaseFactory {
        return super.mongoDbFactory()
    }

    @Bean(name = ["logMongoTemplate"])
    override fun mongoTemplate(
        databaseFactory: MongoDatabaseFactory,
        converter: MappingMongoConverter
    ): MongoTemplate {
        return super.mongoTemplate(databaseFactory, converter)
    }
}