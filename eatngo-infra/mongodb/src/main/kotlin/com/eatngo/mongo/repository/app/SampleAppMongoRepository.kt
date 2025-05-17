package com.eatngo.mongo.repository.app

import com.eatngo.mongo.entity.app.SampleApp
import com.eatngo.mongo.repository.MongoRepository
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class SampleAppMongoRepository: MongoRepository<SampleApp>(
    SampleApp::class.java
) {

    fun findById(id: String): SampleApp? {
        val query = Query()
        query.addCriteria(Criteria.where("_id").`is`(id))
        return super.findOne(query)
    }
}