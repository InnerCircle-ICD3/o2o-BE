package com.eatngo.mongo.config.app

import com.eatngo.mongo.config.AbstractMongoConfiguration
import com.eatngo.mongo.config.MongoConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.eatngo.mongo.repository.app"])
class AppMongoConfiguration: AbstractMongoConfiguration() {
    private var appMongoConfig: MongoConfig = AppMongoConfig()

    public override fun getMongoConfig(): MongoConfig {
        return appMongoConfig
    }

    override fun getDatabaseName(): String {
        return appMongoConfig.database
    }

    @Primary
    @Bean(name = ["appMongoDbFactory"])
    override fun mongoDbFactory(): MongoDatabaseFactory {
        return super.mongoDbFactory()
    }

    @Primary
    @Bean(name = ["appMongoTemplate"])
    override fun mongoTemplate(
        @Qualifier("appMongoDbFactory") databaseFactory: MongoDatabaseFactory,
        converter: MappingMongoConverter
    ): MongoTemplate {
        return super.mongoTemplate(databaseFactory, converter)
    }
}