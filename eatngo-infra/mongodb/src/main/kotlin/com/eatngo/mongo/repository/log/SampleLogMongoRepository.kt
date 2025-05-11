package com.eatngo.mongo.repository.log

import com.eatngo.mongo.entity.log.SampleLog
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class SampleLogMongoRepository: LogMongoRepository<SampleLog>(
    SampleLog::class.java
) {

    fun findById(id: String): SampleLog? {
        val query = Query()
        query.addCriteria(Criteria.where("_id").`is`(id))
        return super.findOne(query)
    }
}