package com.eatngo.mongo.repository.log

import com.eatngo.mongo.repository.AbstractMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate

abstract class LogMongoRepository<T: Any>(
    clazz: Class<T>
): AbstractMongoRepository<T>(clazz) {

    @Autowired
    @Qualifier("logMongoTemplate")
    private lateinit var logMongoTemplate: MongoTemplate

    override fun getMongoTemplate(): MongoTemplate {
        return logMongoTemplate
    }
}