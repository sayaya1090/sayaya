package net.sayaya.login

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockkClass
import net.sayaya.AuthorizationConfig
import net.sayaya.JsonConfig
import net.sayaya.SecurityConfig
import net.sayaya.TokenConfig
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest
@ContextConfiguration(classes = [SecurityConfig::class, JsonConfig::class, SecurityConfigTest.Companion.TestConfig::class])
internal class SecurityConfigTest(private val client: WebTestClient, private val authConfig: AuthorizationConfig): BehaviorSpec({
    Given("서버 기동") {
        /*When("username, password를 Body 필드에 첨부 하여") {
            When("유효한 사용자로 로그인을 시도하면") {
                val param = IdPasswordAuthenticationConverter.Companion.IdPassword("valid", "valid-password")
                val exchange = client.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                    .uri("/login")
                    .bodyValue(param)
                    .exchange()
                var cookie: String? = null
                Then("토큰을 반환하고 redirect URI로 이동한다") {
                    exchange.expectStatus().isEqualTo(HttpStatus.MOVED_PERMANENTLY)
                    exchange.expectCookie() shouldNotBe null
                    exchange.expectCookie().exists(authConfig.authentication)
                    exchange.expectCookie().value(authConfig.authentication) { cookie = it }
                }
                When("로그아웃을 시도하면") {
                    val exchange2 = client.mutateWith(SecurityMockServerConfigurers.csrf()).delete().uri("/logout")
                        .cookie(authConfig.authentication, cookie!!)
                        .exchange()
                    Then("토큰을 만료한다") {
                         exchange2.expectStatus().isEqualTo(HttpStatus.MOVED_PERMANENTLY)
                         exchange2.expectCookie() shouldNotBe null
                         exchange2.expectCookie().maxAge(authConfig.authentication, ZERO)
                    }
                }
            }
            When("유효하지 않은 사용자로 로그인을 시도하면") {
                val param = IdPasswordAuthenticationConverter.Companion.IdPassword("valid", "invalid-password")
                val exchange = client.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                    .uri("/login")
                    .bodyValue(param)
                    .exchange()
                Then("토큰 없이 Unauthorized 상태코드 반환") {
                    exchange.expectStatus().isUnauthorized
                    exchange.expectCookie().doesNotExist(authConfig.authentication)
                }
            }
        }*/
        When("Body에 username 또는 password 없이 로그인을 시도하면") {
            val exchange1 = client.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/login/oauth2/authorization/test?path=post")
                .bodyValue("{\"username:\": \"valid\"}")
                .exchange()
            val exchange2 = client.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/login/oauth2/authorization/test?path=post")
                .bodyValue("{\"password:\": \"valid\"}")
                .exchange()
            Then("토큰 없이 Unauthorized 상태코드 반환") {
                exchange1.expectStatus().isUnauthorized
                exchange1.expectCookie().doesNotExist(authConfig.authentication)
                exchange2.expectStatus().isUnauthorized
                exchange2.expectCookie().doesNotExist(authConfig.authentication)
            }
        }
    }
}) {
    companion object {
        @TestConfiguration
        class TestConfig {
            @Bean fun authorizationConfig(): AuthorizationConfig = AuthorizationConfig().apply {
                authentication = "Authentication"
                loginRedirectUri = "main.html"
                logoutRedirectUri = "login.html"
            }
            @Bean fun tokenConfig() = TokenConfig()
            @Bean fun tokenPublisher() = mockkClass(PublishToken::class).apply {
                val token = "SUCCESS"
                every { publish(any(), any()) } returns Mono.just(token)
            }
            @Bean fun clientRegistrationRepository(): ReactiveClientRegistrationRepository = InMemoryReactiveClientRegistrationRepository(
                ClientRegistration.withRegistrationId("test")
                    .clientId("client-id")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                    .build()
            )
        }
    }
}