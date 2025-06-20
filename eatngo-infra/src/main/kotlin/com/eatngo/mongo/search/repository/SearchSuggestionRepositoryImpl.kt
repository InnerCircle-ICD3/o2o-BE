package com.eatngo.mongo.search.repository

import com.eatngo.mongo.search.entity.SearchSuggestionEntity
import com.eatngo.search.constant.SuggestionType
import com.eatngo.search.domain.SearchSuggestion
import com.eatngo.search.infra.SearchSuggestionRepository
import org.bson.Document
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component

@Component
class SearchSuggestionRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : SearchSuggestionRepository {
    val searchSuggestionIndex = "search-suggestion"

    override fun getSuggestionsByKeyword(
        keyword: String,
        type: SuggestionType?,
        size: Int,
    ): List<SearchSuggestion> {
        val must = mutableListOf<Document>()
        val filter = mutableListOf<Document>()

        val shouldText = mutableListOf<Document>()
        // 검색어 조건을 should로 추가
        shouldText +=
            Document(
                "text",
                Document("query", keyword).append("path", "keyword"),
            )
        shouldText +=
            Document(
                "equals",
                Document("value", keyword).append("path", "keyword.key"),
            )
        shouldText +=
            Document(
                "autocomplete",
                Document("query", keyword).append("path", "keyword"),
            )

        // 검색어가 포함된 키워드만 가져오기 위해 must에 추가

        must +=
            Document(
                "compound",
                Document("should", shouldText)
                    .append("minimumShouldMatch", 1),
            )
        if (type != null) {
            filter.add(
                Document("equals", Document("value", type.code).append("path", "type")),
            )
        }

        val compound = Document()
        if (must.isNotEmpty()) {
            compound.append("must", must)
        }
        if (filter.isNotEmpty()) {
            compound.append("filter", filter)
        }

        val searchOp =
            AggregationOperation {
                Document(
                    "\$search",
                    Document()
                        .append("index", searchSuggestionIndex)
                        .append(
                            "compound",
                            compound,
                        ),
                )
            }
        val limitOp = Aggregation.limit(size.toLong())
        val pipeline =
            Aggregation.newAggregation(
                searchOp,
                limitOp,
            )
        val results =
            mongoTemplate
                .aggregate(
                    pipeline,
                    "SearchSuggestion",
                    SearchSuggestionEntity::class.java,
                ).mappedResults

        return results.map { it.to() }
    }

    override fun saveSuggestionList(suggestList: List<SearchSuggestion>) {
        val bulkOps =
            mongoTemplate.bulkOps(
                BulkOperations.BulkMode.UNORDERED,
                SearchSuggestionEntity::class.java,
                "SearchSuggestion",
            )
        suggestList.forEach { suggestion ->
            val entity = SearchSuggestionEntity.from(suggestion)
            bulkOps.upsert(
                Query(Criteria.where("id").`is`(entity.id)),
                Update()
                    .setOnInsert("keyword", entity.keyword)
                    .setOnInsert("type", entity.type)
                    .setOnInsert("keywordId", entity.keywordId),
            )
        }

        bulkOps.execute()
    }

    override fun saveSuggestion(suggestion: SearchSuggestion) {
        val entity = SearchSuggestionEntity.from(suggestion)
        mongoTemplate.save(entity, "SearchSuggestion")
    }

    override fun deleteByKeywordIdList(keywordIdList: List<Long>) {
        if (keywordIdList.isEmpty()) {
            return
        }
        val query = Query(Criteria.where("keywordId").`in`(keywordIdList))
        mongoTemplate.remove(query, SearchSuggestionEntity::class.java, "SearchSuggestion")
    }
}
