package com.eatngo.mongo.entity.log

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document("sample_log")
class SampleLog {
    @Field("_id")
    private var id: String = ""
    @Field("name")
    private var name: String? = ""
}
