package net.sayaya.search

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.sql.SqlIdentifier
import reactor.core.publisher.Mono

interface Searchable {
    fun R2dbcEntityTemplate.predicates(filters: List<Pair<String, String>>): Criteria {
        if(filters.isEmpty()) return Criteria.empty()
        return filters.map { (key, value) -> predicate(key, value) }.reduce(Criteria::and)
    }
    fun R2dbcEntityTemplate.predicate(key: String, value: String): Criteria
    fun <T> R2dbcEntityTemplate.search (from: SqlIdentifier, filters: List<Pair<String, String>>, clazz: Class<T>,  pageable: Pageable): Mono<Page<T>> {
        val predicates = predicates(filters)
        val count = count(Query.query(predicates), clazz)
        val query = Query.query(predicates).with(pageable)
        val data = select(clazz).from(from).matching(query).all().collectList()
        return count.zipWith(data).map { PageImpl(it.t2, pageable, it.t1) }
    }
}