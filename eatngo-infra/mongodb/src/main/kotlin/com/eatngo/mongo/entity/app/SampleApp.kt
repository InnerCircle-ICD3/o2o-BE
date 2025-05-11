package com.eatngo.mongo.entity.app

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document("sample_app")
class SampleApp {
    @Field("_id")
    private var id: String = ""
    @Field("name")
    private var name: String? = ""
}