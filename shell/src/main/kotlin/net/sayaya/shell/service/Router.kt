package net.sayaya.shell.service

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration("net.sayaya.service.Router")
class Router(val handler: Handler) {
    @Bean("net.sayaya.service.Router.route")
    fun route() = router {
        GET("/menu", accept(MediaType("application", "vnd.sayaya.v1+json")), ::findMenuFromServices)
    }
    private fun findMenuFromServices(request: ServerRequest): Mono<ServerResponse> = request.cookies()
        .let(handler::list).collectList()
        .flatMap(ServerResponse.ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(Mono.defer { ServerResponse.noContent().build() })
        .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
}