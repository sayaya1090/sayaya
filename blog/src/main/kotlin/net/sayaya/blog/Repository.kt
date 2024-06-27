package net.sayaya.blog

import net.sayaya.client.data.Article
import net.sayaya.data.Search
import net.sayaya.search.Searchable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.sql.SqlIdentifier
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class Repository(private val template: R2dbcEntityTemplate): Searchable {
    fun search(param: Search): Mono<Page<Catalog>> {
        val pageNumber = param.page ?: 0
        val pageSize = param.limit ?: 10
        val sortBy = param.sortBy?.let(::property) ?: "created_at"
        val sort = (param.asc?.let { if(it) Sort.Order.asc(sortBy) else Sort.Order.desc(sortBy) } ?: Sort.Order.asc(sortBy)).let { Sort.by(it) }
        val pageable = PageRequest.of(pageNumber, pageSize, sort)
        param.filters.addFirst("published" to "true")
        return template.search(SqlIdentifier.unquoted("catalog"), param.filters, Catalog::class.java, pageable)
    }
    private fun property(name: String): String? = when(name) {
        "title" -> "title"
        "createdAt" -> "created_at"
        "updatedAt" -> "updated_at"
        "publishedAt" -> "published_at"
        else -> null
    }
    override fun R2dbcEntityTemplate.predicate(key: String, value: String): Criteria = when(key) {
        "query" ->  predicate("title", value).or(
                    predicate("author", value)).or(
                    predicate("description", value)).or(
                    predicate("tags", value))
        "published" -> Criteria.where("published_at").isNotNull
        "title" -> Criteria.where("title").like("%$value%")
        "author" -> Criteria.where("author_alias").like("%$value%")
        "description" -> Criteria.where("description").like("%$value%")
        "tags" -> Criteria.where("tags @> '{$value}'").isTrue
        else -> property(key)?.let { Criteria.where(it).`is`(value).ignoreCase(true) } ?: Criteria.empty()
    }
    fun findByUserAndTitle(id: String, title: String): Mono<Article> =
        Query.query(Criteria.where("author_alias").`is`(id).and("title").`is`(title).and("published_at").isNotNull).let {
            template.selectOne(it, Article::class.java)
        }
}
