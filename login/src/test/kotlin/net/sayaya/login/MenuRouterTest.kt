package net.sayaya.login

import io.kotest.core.spec.style.BehaviorSpec
import net.sayaya.JsonConfig
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest
@ContextConfiguration(classes = [ Router::class, MenuRouterTest.Companion.SecurityConfig::class, JsonConfig::class ])
internal class MenuRouterTest(private val client: WebTestClient): BehaviorSpec({
    Given("서버 기동") {
        When("메뉴를 요청하면") {
            val request = client.get().uri("/menu")
            Then("로그인 메뉴를 출력한다") {
                request.exchange()
                       .expectStatus().isOk
                       .expectBody().jsonPath("$.title").isEqualTo("SIGN IN")
            }
        }
    }
}){
    companion object {
        @TestConfiguration
        @EnableWebFluxSecurity
        class SecurityConfig {
            @Bean fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http {
                anonymous {  }
            }
        }
    }
}