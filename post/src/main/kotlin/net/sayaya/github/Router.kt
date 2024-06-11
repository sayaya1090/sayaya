package net.sayaya.github

import net.sayaya.client.data.GithubAppConfig
import net.sayaya.client.data.GithubConfigRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration("net.sayaya.github.Router")
class Router(private val handler: Handler) {
    @Bean("net.sayaya.app.AppRouter.route")
    fun route() = router {
        GET("/post/destination", accept(MediaType("application", "vnd.sayaya.v1+json")), ::findUserGithubRepoConfig)
        PUT("/post/destination", accept(MediaType("application", "vnd.sayaya.v1+json")), ::saveUserGitHubRepoConfig)
        GET ("/post/repositories", accept(MediaType("application", "vnd.sayaya.v1+json")), ::repositories)
        POST("/post/repositories", accept(MediaType("application", "vnd.sayaya.v1+json")), ::repositories)
        GET ("/post/repositories/{owner}/{repository}/branches", accept(MediaType("application", "vnd.sayaya.v1+json")), ::branches)
        POST("/post/repositories/{owner}/{repository}/branches", accept(MediaType("application", "vnd.sayaya.v1+json")), ::branches)
    }
    private fun findUserGithubRepoConfig(request: ServerRequest): Mono<ServerResponse> = request.principal()
        .flatMap { principal -> UUID.fromString(principal.name).let(handler::findGithubRepoConfig) }
        .flatMap(ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(noContent().build())
    private fun saveUserGitHubRepoConfig(request: ServerRequest): Mono<ServerResponse> = request.principal()
        .zipWith(request.bodyToMono(GithubConfigRequest::class.java).flatMap { config ->
            if(config.appId==null) request.findGithubAppConfigFromPrincipal().map { config.appId(it.appId).installationId(it.installationId).privateKey(it.privateKey) }
            else Mono.just(config)
        }).flatMap {
            val (principal, config) = it.t1 to it.t2
            handler.saveGithubAppConfig(UUID.fromString(principal.name), config)
        }.flatMap(ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(noContent().build())
    private fun ServerRequest.findGithubAppConfigFromPrincipal(): Mono<GithubAppConfig> = principal()
        .mapNotNull { principal -> UUID.fromString(principal.name) }
        .flatMap(handler::findGithubAppConfigFromUserSecret)
    private fun ServerRequest.findGithubAppConfigFromBodyOrPrincipal(): Mono<GithubAppConfig> = bodyToMono(GithubAppConfig::class.java)
        .switchIfEmpty(Mono.defer { findGithubAppConfigFromPrincipal() })
    private class NoAppConfigException(msg: String): AuthenticationException(msg)
    private fun repositories(request: ServerRequest): Mono<ServerResponse> = request.findGithubAppConfigFromBodyOrPrincipal()
        .switchIfEmpty(Mono.error(NoAppConfigException("App configuration required")))
        .flatMap(handler::repositories)
        .flatMap (ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(noContent().build())
        .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
        .onErrorResume(NoAppConfigException::class.java) { ServerResponse.status(HttpStatus.PRECONDITION_REQUIRED).bodyValue(it.localizedMessage) }
    private fun branches(request: ServerRequest): Mono<ServerResponse> = request.findGithubAppConfigFromBodyOrPrincipal()
        .switchIfEmpty(Mono.error(NoAppConfigException("App configuration required")))
        .map { auth ->
            val owner = request.pathVariable("owner")
            val repo = request.pathVariable("repository")
            checkNotNull(auth)
            GithubConfigRequest().appId(auth.appId).installationId(auth.installationId).privateKey(auth.privateKey).owner(owner).repo(repo)
        }.flatMapMany(handler::branches).collectList().filter(List<String>::isNotEmpty)
        .flatMap(ok().contentType(MediaType("application", "vnd.sayaya.v1+json"))::bodyValue)
        .switchIfEmpty(noContent().build())
        .onErrorResume(IllegalStateException::class.java) { ex -> ServerResponse.badRequest().bodyValue(ex.message?:"") }
        .onErrorResume(WebClientResponseException::class.java) { ex -> ServerResponse.status(ex.statusCode).bodyValue(ex.responseBodyAsString) }
        .onErrorResume(NoAppConfigException::class.java) { ServerResponse.status(HttpStatus.PRECONDITION_REQUIRED).bodyValue(it.localizedMessage) }
}