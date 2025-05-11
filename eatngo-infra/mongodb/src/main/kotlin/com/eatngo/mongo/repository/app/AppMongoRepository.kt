package com.eatngo.mongo.repository.app

import com.eatngo.mongo.repository.AbstractMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate

abstract class AppMongoRepository<T : Any>(
    clazz: Class<T>
) : AbstractMongoRepository<T>(clazz) {

    @Autowired
    @Qualifier("appMongoTemplate")
    private lateinit var appMongoTemplate: MongoTemplate

    override fun getMongoTemplate(): MongoTemplate {
        return appMongoTemplate
    }
}