package com.eatngo.mongo.repository

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * MongoDB Repository Base Class
 */
abstract class MongoRepository<T: Any> (
    private val clazz: Class<T>
) {

    @Autowired
    @Qualifier("mongoTemplate")
    private lateinit var mongoTemplate: MongoTemplate

    fun findById(id: ObjectId): T? {
        return mongoTemplate.findById(id, clazz)
    }

    fun findOne(query: Query): T? {
        return mongoTemplate.findOne<T?>(query, clazz)
    }

    fun findAll(): List<T> {
        return mongoTemplate.findAll(clazz)
    }

    fun save(entity: T): T {
        mongoTemplate.save(entity)
        return entity
    }
}