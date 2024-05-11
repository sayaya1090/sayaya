package net.sayaya.login

import net.sayaya.client.data.Menu
import net.sayaya.client.data.Page
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.authentication.AnonymousAuthenticationToken
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
    private fun findMenu(request: ServerRequest): Mono<ServerResponse> = request.principal()
        .map { if(it is AnonymousAuthenticationToken) login else logout }
        .flatMap(ServerResponse.ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(Mono.defer { ServerResponse.noContent().build() })
        .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }

    companion object {
        private val login = Menu().apply {
            title = "SIGN IN"
            icon = "fa-right-to-bracket"
            iconType = "sharp"
            bottom = true
            order = "Z000"
            children = arrayOf(
                Page().apply {
                    title = "-"
                    order = "Z000-1"
                    uri = "/login"
                    icon = "fa-right-to-bracket"
                }
            )
        }
        private val logout = Menu().apply {
            title = "SIGN OUT"
            icon = "fa-left-from-bracket"
            iconType = "sharp"
            bottom = true
            order = "Z000"
            children = arrayOf(
                Page().apply {
                    title = "-"
                    order = "Z000-1"
                    uri = "/oauth2/logout"
                    icon = "fa-left-from-bracket"
                }
            )
        }
    }
}