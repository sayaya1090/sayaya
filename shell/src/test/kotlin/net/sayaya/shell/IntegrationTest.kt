package net.sayaya.shell

import com.fasterxml.jackson.databind.ObjectMapper
import io.fabric8.kubernetes.client.KubernetesClient
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@SpringBootTest(properties = [
    "spring.main.cloud-platform=KUBERNETES",
    "spring.cloud.kubernetes.discovery.service-labels.type=service",
    "spring.cloud.kubernetes.discovery.namespaces=test"
]) @ContextConfiguration(classes = [KubernetesMockConfiguration::class, IntegrationTest.Companion.TestWebConfig::class])
@AutoConfigureWebTestClient
internal class IntegrationTest (
    private val k8s: KubernetesClient,
    private val client: WebTestClient
): BehaviorSpec({
    with(KubernetesMockConfiguration) {
        extensions(SpringExtension)
        Given("지정된 네임스페이스 내에 지정된 타입의 레이블을 가진 N개의 service가 배포된 k8s 내에서") {
            k8s.createServiceAndPod("test", "svc1", "172.30.1.1", mapOf("type" to "service"), 3)
            k8s.createServiceAndPod("test", "svc2", "172.30.1.2", mapOf("type" to "service"))
            k8s.createServiceAndPod("test", "svc-no-pod", "172.30.1.3", mapOf("type" to "service"), 0)
            k8s.createServiceAndPod("test", "svc4", "172.30.1.4", mapOf("type" to "actuator"), 2)
            k8s.createServiceAndPod("test", "svc5", "172.30.1.5")
            k8s.createServiceAndPod("other", "svc6", "172.30.1.6", mapOf("type" to "service"))
            When("인증 정보와 함께 서비스 목록을 요청하면") {
                val exchange = client.get().uri("/menu").cookie(AUTHENTICATION, "key").exchange()
                Then("파드가 존재하는 서비스의 응답이 목록으로 반환된다.") {
                    exchange.expectStatus().isOk
                        .expectBodyList(Map::class.java).hasSize(2)
                }
            }
            When("인증 정보 없이 서비스 목록을 요청하면") {
                val exchange = client.get().uri("/menu").exchange()
                Then("빈 응답이 반환된다") {
                    exchange.expectStatus().isOk
                        .expectBodyList(Map::class.java).hasSize(0)
                }
            }
        }
    }
}) {
    companion object {
        private const val AUTHENTICATION = "Authentication"
        @TestConfiguration
        internal class TestWebConfig(private val objectMapper: ObjectMapper) {
            @Bean @Primary fun clientMock(): WebClient = WebClient.builder().exchangeFunction { request -> Mono.defer {
                if(request.cookies().containsKey(AUTHENTICATION)) {
                    val svc = request.url().host
                    Mono.just(ClientResponse.create(OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(Menu(svc)))
                        .build())
                } else Mono.just(ClientResponse.create(UNAUTHORIZED).build())
            }}.build()
        }
        data class Menu(val title: String)
    }
}