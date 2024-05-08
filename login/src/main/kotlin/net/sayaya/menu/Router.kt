package net.sayaya.menu

import net.sayaya.client.data.Menu
import net.sayaya.client.data.Page
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class Router {
    @Bean("net.sayaya.menu.Router.route")
    fun route() = router {
        GET ("/menu", ::findMenu)
    }
    private val menu = Menu().apply {
        title = "Log In"
        icon = "fa-right-to-bracket"
        // supportingText = ""
        order = "Z000"
        children = arrayOf(
            Page().apply {
                title = "-"
                order = "Z000-1"
                uri = "/login.html"
            }
        )
    }
    private fun findMenu(request: ServerRequest): Mono<ServerResponse> = Mono.just(menu)
        .flatMap(ServerResponse.ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(Mono.defer { ServerResponse.noContent().build() })
        .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
}