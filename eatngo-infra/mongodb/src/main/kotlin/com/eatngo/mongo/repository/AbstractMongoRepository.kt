package com.eatngo.mongo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

abstract class AbstractMongoRepository<T: Any> (
    private val clazz: Class<T>
) {

    protected abstract fun getMongoTemplate(): MongoTemplate;

    fun findById(id: ObjectId): T? {
        return getMongoTemplate().findById(id, clazz)
    }

    fun findOne(query: Query): T? {
        return this.getMongoTemplate().findOne<T?>(query, clazz)
    }

    fun findAll(): List<T> {
        return getMongoTemplate().findAll(clazz)
    }

    fun save(entity: T): T {
        getMongoTemplate().save(entity)
        return entity
    }
}