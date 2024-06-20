package net.sayaya.search

import net.sayaya.data.Search
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.sql.SqlIdentifier
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
class Repository(private val template: R2dbcEntityTemplate): Searchable {
    fun search(param: Search): Mono<Page<Catalog>> {
        val pageNumber = param.page ?: 0
        val pageSize = param.limit ?: 10
        val sortBy = param.sortBy?.let(::property) ?: "created_at"
        val sort = (param.asc?.let { if(it) Sort.Order.asc(sortBy) else Sort.Order.desc(sortBy) } ?: Sort.Order.asc(sortBy)).let { Sort.by(it) }
        val pageable = PageRequest.of(pageNumber, pageSize, sort)
        return template.search(SqlIdentifier.unquoted("catalog"), param.filters, Catalog::class.java, pageable)
    }
    private fun property(name: String): String? = when(name) {
        "title" -> "title"
        "createdAt" -> "created_at"
        "updatedAt" -> "updated_at"
        "publishedAt" -> "published_at"
        else -> null
    }
    override fun R2dbcEntityTemplate.predicate(key: String, value: String): Criteria {
        if(key == "author") return Criteria.where("author").`is`(UUID.fromString(value))
        val property = property(key) ?: return Criteria.empty()
        return Criteria.where(property).`is`(value).ignoreCase(true)
    }
}
