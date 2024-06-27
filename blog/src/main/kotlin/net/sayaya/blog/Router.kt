package net.sayaya.blog

import net.sayaya.data.Search
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration("net.sayaya.blog.Router")
class Router(private var handler: Handler) {
    @Bean("net.sayaya.blog.Router.route")
    fun route() = router {
        GET("/") {
            if(it.headers().accept().contains(MediaType("application", "vnd.sayaya.v1+json"))) search(it)
            else index(it)
        }
        GET("/{id}/{title}", ::find)
    }
    private fun search(request: ServerRequest): Mono<ServerResponse> = with(Search) { request.param() }
        .let { param -> handler.search(param) }
        .flatMap { posts ->
            ServerResponse.ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))
                .header("X-Total-Count", posts.totalElements.toString())
                .header("X-Total-Page", posts.totalPages.toString())
                .bodyValue(posts.toList())
        }.switchIfEmpty(ServerResponse.noContent().build()).
        onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }.
        onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
    private fun index(request: ServerRequest): Mono<ServerResponse> = with(Search) { request.param() }
        .let { param -> handler.search(param) }.flatMap {
            ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("catalog", mapOf("articles" to it.content))
        }.switchIfEmpty(Mono.defer { ServerResponse.noContent().build() })
        .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }

    private fun find(request: ServerRequest): Mono<ServerResponse> = handler.find(request.pathVariable("id"), request.pathVariable("title"))
        .flatMap {
            ServerResponse.ok().contentType(MediaType.TEXT_HTML).
            render("article", mapOf("catalog" to it, "content" to it.html))
        }.switchIfEmpty(Mono.defer { ServerResponse.noContent().build() })
            .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
            .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
}