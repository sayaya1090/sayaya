package net.sayaya.search

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import net.sayaya.JsonConfig
import net.sayaya.R2dbcConfig
import net.sayaya.github.RouterWithUserHasGithubSecretTest.Companion.USER_ID
import net.sayaya.search.testcontainers.Database
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@ContextConfiguration(classes = [ Router::class, Handler::class, Repository::class, JsonConfig::class, R2dbcConfig::class ])
@WebFluxTest
@AutoConfigureDataR2dbc
@WithMockUser(username = USER_ID)
internal class SearchTest(client : WebTestClient): BehaviorSpec({
    extensions(SpringExtension)
    Given("저장한 Post가 없는 사용자 계정으로") {
        When("카탈로그 목록을 요청하면") {
            val request = client.get().uri("/posts")
            Then("No Content가 반환된다") {
                val exchange = request.exchange()
                exchange.expectStatus().isNoContent
            }
        }
    }
}) {
    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            Database.registerDynamicProperties(registry)
        }
    }
}