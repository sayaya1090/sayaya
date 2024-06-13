package net.sayaya.post


import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
class PostRepository(private val template: R2dbcEntityTemplate) {
    fun save(post: Post): Mono<Post> = if(post.isNew()) template.insert(post) else template.update(post)
    fun update(post: Post, tags: Collection<String>): Mono<Post> = template.update(post)
        .flatMap { p ->
            template.delete(Query.query(Criteria.where("post").`is`(p.id)), PostKeyword::class.java).
            flatMap { Flux.fromIterable(tags).flatMap { tag -> template.insert(PostKeyword(p.id, tag)) }.then() }.
            thenReturn(p)
        }
    fun find(id: UUID): Mono<Post> = template.selectOne (Query.query (Criteria.where("id").`is`(id)), Post::class.java)
    fun findKeyword(id: UUID): Mono<List<PostKeyword>> = template.select(PostKeyword::class.java)
        .matching(Query.query(Criteria.where("post").`is`(id)))
        .all().collectList()
}
