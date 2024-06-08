package net.sayaya.shell.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient
import org.springframework.http.HttpCookie
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Service("net.sayaya.service.Handler")
class Handler(
    private val discovery: ReactiveDiscoveryClient,
    private val client: WebClient
) {
    fun list(cookies: Map<String, List<HttpCookie>>): Flux<Any> {
        val cookiesMap = cookies.map { e->e.key to e.value.stream().map { it.value }.toList() }.toMap()
        return discovery.services
            .flatMap { svc ->
                println("${LocalDateTime.now()} Service: $svc")
                routes(svc, cookiesMap) }
            .onErrorContinue(Exception::class.java) { e, _ -> e.printStackTrace() }
    }
    fun routes(service: String, cookies: Map<String, List<String>>): Mono<Any> = discovery
        .getInstances(service).next()
        .flatMap { svc ->
            println("${LocalDateTime.now()} Service picked: $svc")
            client.get().uri(svc.uri.resolve("/menu"))
            .cookies{ cookies.forEach { cookie -> it.addAll(cookie.key, cookie.value) } }
            .retrieve()
            .toEntity<Any>()
            .onErrorResume(WebClientResponseException::class.java) { if(it.statusCode == HttpStatus.UNAUTHORIZED) Mono.empty() else Mono.error(it) }
            .filter {
                println("${LocalDateTime.now()} Service: $svc, statusCode: ${it.statusCode}")
                it.statusCode.is2xxSuccessful }
            .map { it.body!! }
            .timeout(Duration.ofMillis(1500))
        }
}