package net.sayaya.post

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import net.sayaya.JsonConfig
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest
@ContextConfiguration(classes = [ Router::class, JsonConfig::class ])
internal class MenuRouterWithAnonymousTest(private val client: WebTestClient): BehaviorSpec({
    extensions(SpringExtension)
    Given("인증되지 않은 사용자가") {
        When("메뉴를 요청하면") {
            val request = client.get().uri("/menu")
            Then("로그인 메뉴를 출력한다") {
                request.exchange()
                       .expectStatus().isUnauthorized
                    .expectBody().returnResult().responseBody.let { println(it) }
            }
        }
    }
})