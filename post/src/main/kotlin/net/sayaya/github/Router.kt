package net.sayaya.github

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

@Configuration("net.sayaya.github.Router")
class Router(
    private val authHandler: GithubAuthHandler,
    private val repoHandler: GithubRepoHandler
)  {
    /*@Bean("net.sayaya.app.AppRouter.route")
    fun route() = router {
        GET ("/post/destination", accept(MediaType("application", "vnd.sayaya.v1+json")), ::findUserGitHubAppConfig)
        PUT ("/post/destination", accept(MediaType("application", "vnd.sayaya.v1+json")), ::saveUserGitHubAppConfig)
        GET ("/post/repositories", accept(MediaType("application", "vnd.sayaya.v1+json")), ::repositories)
        POST("/post/repositories", accept(MediaType("application", "vnd.sayaya.v1+json")), ::repositories)
        GET ("/post/repositories/{owner}/{repository}/branches", accept(MediaType("application", "vnd.sayaya.v1+json")), ::branches)
        POST("/post/repositories/{owner}/{repository}/branches", accept(MediaType("application", "vnd.sayaya.v1+json")), ::branches)
    }
    private fun findUserGitHubAppConfig(request: ServerRequest): Mono<ServerResponse> = request.principal()
        .flatMap { principal -> UUID.fromString(principal.name).let(authHandler::findGitHubAppConfig) }
        .flatMap (ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(noContent().build())

    private fun saveUserGitHubAppConfig(request: ServerRequest): Mono<ServerResponse> = request.principal()
        .flatMap { principal ->
            request.bodyToMono(GithubAppConfigRequest::class.java).
            flatMap { authHandler.saveGitHubAppConfig(UUID.fromString(principal.name), it) }
        }.flatMap (ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(noContent().build())

    private fun ServerRequest.findGitHubAuthFromPrincipal(): Mono<GitHubAppAuth> = principal()
        .mapNotNull { principal -> UUID.fromString(principal.name) }
        .flatMap(authHandler::findGitHubAuthFromUserSecret)
    private fun ServerRequest.findGitHubAuthFromPrincipalOrBody(): Mono<GitHubAppAuth> = findGitHubAuthFromPrincipal()
        .switchIfEmpty(Mono.defer { bodyToMono(GitHubAppAuth::class.java) })

    private fun repositories(request: ServerRequest): Mono<ServerResponse> = request.findGitHubAuthFromPrincipalOrBody()
        .switchIfEmpty(Mono.error(AuthenticationCredentialsNotFoundException("Authentication required")))
        .flatMap(repoHandler::repositories)
        .flatMap (ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(noContent().build())
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }

    private fun branches(request: ServerRequest): Mono<ServerResponse> = request.findGitHubAuthFromPrincipalOrBody()
        .switchIfEmpty(Mono.error(AuthenticationCredentialsNotFoundException("Authentication required")))
        .map { auth ->
            val owner = request.pathVariable("owner")
            val repo = request.pathVariable("repository")
            checkNotNull(auth)
            GitHubRepositoryRequest(owner = owner, repo = repo, auth = auth)
        }.flatMapMany(repoHandler::branches).collectList().filter(List<String>::isNotEmpty)
        .flatMap(ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(noContent().build())
        .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }*/
}