package net.sayaya.post

import net.sayaya.client.data.Menu
import net.sayaya.client.data.Page
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
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
        .switchIfEmpty(Mono.error(AuthenticationCredentialsNotFoundException("Authentication required")))
        .thenReturn(post)
        .flatMap(ServerResponse.ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(Mono.defer { ServerResponse.noContent().build() })
        .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }

    companion object {
        private val post = Menu().apply {
            title = "POST"
            supportingText = "New Post"
            icon = "fa-blog"
            iconType = "sharp"
            order = "C000"
            children = arrayOf(
                Page().apply {
                    title = "New"
                    order = "C000-1"
                    uri = "/post#new"
                    icon = "fa-pen-to-square"
                }, Page().apply {
                    title = "List"
                    order = "C000-5"
                    uri = "/post"
                    icon = "fa-list"
                }
            )
        }
    }
}