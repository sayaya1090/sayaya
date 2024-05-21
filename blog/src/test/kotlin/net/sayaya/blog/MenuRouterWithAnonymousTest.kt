package net.sayaya.blog

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.mockk
import net.sayaya.JsonConfig
import net.sayaya.authentication.JwtAuthenticationConverter
import net.sayaya.authentication.JwtAuthenticationManager
import net.sayaya.authentication.SecurityConfig
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest
@ContextConfiguration(classes = [ Router::class, JsonConfig::class, SecurityConfig::class, MenuRouterWithAnonymousTest.Companion.TestConfig::class ])
internal class MenuRouterWithAnonymousTest(private val client: WebTestClient): BehaviorSpec({
    extensions(SpringExtension)
    Given("인증되지 않은 사용자가") {
        When("메뉴를 요청하면") {
            val request = client.get().uri("/menu")
            Then("로그인 메뉴를 출력한다") {
                request.exchange()
                    .expectStatus().isOk
                    .expectBody().jsonPath("$.title").isEqualTo("ARTICLE")
            }
        }
    }
}) {
    companion object {
        @TestConfiguration
        class TestConfig {
            @Bean fun jwtAuthenticationManager(): JwtAuthenticationManager = mockk()
            @Bean fun jwtAuthenticationConverter(): JwtAuthenticationConverter = mockk<JwtAuthenticationConverter>().apply {
                every { convert(any()) } returns Mono.empty()
            }
        }
    }
}