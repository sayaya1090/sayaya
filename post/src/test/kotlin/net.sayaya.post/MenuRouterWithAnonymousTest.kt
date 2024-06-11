package net.sayaya.post

import io.kotest.core.spec.style.BehaviorSpec
import net.sayaya.JsonConfig
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest
@ContextConfiguration(classes = [ Router::class, JsonConfig::class ])
internal class MenuRouterWithAnonymousTest(private val client: WebTestClient): BehaviorSpec({
    Given("인증되지 않은 사용자가") {
        When("메뉴를 요청하면") {
            val request = client.get().uri("/menu")
            Then("Unauthorized 를 반환한다") {
                request.exchange().expectStatus().isUnauthorized
            }
        }
    }
})