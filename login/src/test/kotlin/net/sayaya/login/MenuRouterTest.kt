package net.sayaya.login

import io.kotest.core.spec.style.BehaviorSpec
import net.sayaya.JsonConfig
import net.sayaya.menu.Router
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(excludeAutoConfiguration = [ ReactiveSecurityAutoConfiguration::class ])
@ContextConfiguration(classes = [ Router::class, JsonConfig::class ])
internal class MenuRouterTest(private val client: WebTestClient): BehaviorSpec({
    Given("서버 기동") {
        When("메뉴를 요청하면") {
            val request = client.get().uri("/menu").exchange()
            Then("메뉴를 출력한다") {
                request.expectStatus().isOk
                       .expectBody().jsonPath("$.title").isEqualTo("Log In")
            }
        }
    }
})