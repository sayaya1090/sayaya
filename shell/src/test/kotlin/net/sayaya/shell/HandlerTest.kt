package net.sayaya.shell

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import net.sayaya.shell.service.Handler
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

internal class HandlerTest: BehaviorSpec({
    val authentication = "Authentication"
    val client = WebClient.builder().exchangeFunction { request -> Mono.defer {
        if(request.cookies().containsKey(authentication)) {
            val svc = request.url().host
            Mono.just(ClientResponse.create(OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body("{\"title\":\"$svc\"}").build())
        } else Mono.just(ClientResponse.create(UNAUTHORIZED).build())
    }} .build()
    val discovery = mockk<ReactiveDiscoveryClient>().apply {
        every { services } returns( Flux.just("svc1", "svc2", "svc3") )
        every { getInstances(any()) } answers {
            val svc = it.invocation.args[0] as String
            val instance = object: ServiceInstance {
                override fun getServiceId(): String = svc
                override fun getHost() = svc
                override fun getPort() = 80
                override fun isSecure() = false
                override fun getUri() = URI("http://$svc")
                override fun getMetadata() = emptyMap<String, String>()
            }
            Flux.just(instance)
        }
    }
    Given("서비스 N개가 구동 중에") {
        val handler = Handler(discovery, client)
        When("인증 정보 없이 메뉴 목록을 요청하면") {
            val flux = handler.list(emptyMap())
            Then("빈 결과를 리턴한다") {
                val list = flux.collectList().block()
                list!!.size shouldBe 0
            }
        }
        When("인증 정보가 주어지면") {
            val flux = handler.list(mapOf(authentication to listOf(HttpCookie(authentication, "key"))))
            Then("N개 결과를 리턴한다") {
                val list = flux.collectList().block()
                list!!.size shouldBe 3
            }
        }
    }
})