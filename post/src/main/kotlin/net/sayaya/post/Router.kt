package net.sayaya.post

import net.sayaya.client.data.CatalogItem
import net.sayaya.client.data.Menu
import net.sayaya.client.data.Page
import net.sayaya.client.data.PostRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration("net.sayaya.post.Router")
class Router(private val handler: Handler) {
    @Bean("net.sayaya.post.Router.route")
    fun route() = router {
        PUT("/posts", accept(MediaType("application", "vnd.sayaya.v1")), ::post)
        PATCH("/posts/{id}", accept(MediaType("application", "vnd.sayaya.v1")), ::catalog)
        GET("/posts/{id}", accept(MediaType("application", "vnd.sayaya.v1+json")), ::find)
    }
    private fun post(request: ServerRequest): Mono<ServerResponse> = request.principal().zipWith(request.bodyToMono(PostRequest::class.java))
        .flatMap { tuple ->
            val (principal, post) = tuple.t1 to tuple.t2
            handler.post(principal.name, post)
        }.flatMap { ok().contentType(MediaType("application", "vnd.sayaya.v1")).bodyValue(it.toString()) }.
        switchIfEmpty(noContent().build()).
        onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }.
        onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
    private fun catalog(request: ServerRequest): Mono<ServerResponse> = request.principal().zipWith(request.bodyToMono(CatalogItem::class.java))
        .flatMap { tuple ->
            val (principal, meta) = tuple.t1 to tuple.t2
            handler.patch(principal.name, UUID.fromString(request.pathVariable("id")), meta)
        }.flatMap { ok().contentType(MediaType("application", "vnd.sayaya.v1")).bodyValue(it.toString()) }.
        switchIfEmpty(noContent().build()).
        onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }.
        onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
    private fun find(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id")
        return request.principal().flatMap { principal -> handler.find(principal.name, UUID.fromString(id)) }.
        flatMap { post -> ok().contentType(MediaType("application", "vnd.sayaya.v1+json")).bodyValue(post) }.
        switchIfEmpty(noContent().build()).
        onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }.
        onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
    }
}