package net.sayaya.search

import net.sayaya.data.Search
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration("net.sayaya.search.Router")
class Router(private val handler: Handler) {
    @Bean("net.sayaya.search.Router.route")
    fun route() = router {
        GET("/posts", accept(MediaType("application", "vnd.sayaya.v1+json")), ::search)
    }
    private fun search(request: ServerRequest): Mono<ServerResponse> = request.principal()
        .flatMap { with(Search) {
            request.param().apply {
                filters.add("author" to it.name)
            }
        } .let { param -> handler.search(param)} }
        .flatMap { posts ->
            ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))
                .header("X-Total-Count", posts.totalElements.toString())
                .header("X-Total-Page", posts.totalPages.toString())
                .bodyValue(posts.toList())
        }.switchIfEmpty(noContent().build()).
        onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }.
        onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
}