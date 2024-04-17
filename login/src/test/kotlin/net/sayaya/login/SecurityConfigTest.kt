package net.sayaya.login

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import net.sayaya.AuthorizationConfig
import net.sayaya.SecurityConfig
import net.sayaya.TokenConfig
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Mono
import java.time.Duration

@WebFluxTest
@ContextConfiguration(classes = [SecurityConfig::class, SecurityConfigTest.Companion.TestConfig::class])
@Testcontainers
internal class SecurityConfigTest(private val client: WebTestClient, private val authConfig: AuthorizationConfig): BehaviorSpec({
    Given("서버 기동") {
        When("로그인 URL을 요청하면") {
            val request = client.get().uri("/oauth2/authorization/$PROVIDER").exchange()
            val loginUrl by lazy { request.returnResult<Any>().responseHeaders.location }
            val session by lazy { request.returnResult<Any>().responseCookies["SESSION"]!!.first().value }
            Then("로그인 페이지 정보 제공") {
                request.expectStatus().isFound
                request.expectCookie().exists("SESSION")
                request.expectCookie().doesNotExist(authConfig.authentication)
                request.expectHeader().exists("location")
            }
            And("전달된 로그인 페이지에서 로그인 성공하면") {
                val authentication = WebClient.create().post().uri(loginUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(LinkedMultiValueMap<String, String>().apply {
                        set("username", USER)
                    }).retrieve().toBodilessEntity().map { response ->
                        response.statusCode shouldBe HttpStatus.FOUND
                        response.headers.location
                    }.block()
                val publishToken = client.get().uri(checkNotNull(authentication)).cookie("SESSION", session).exchange()
                Then("토큰 발급") {
                    publishToken.expectCookie().valueEquals(authConfig.authentication, TOKEN)
                    publishToken.expectCookie().httpOnly(authConfig.authentication, true)
                    publishToken.expectCookie().secure(authConfig.authentication, true)
                }
                Then("loginRedirectUri로 리다이렉트") {
                    publishToken.expectStatus().isFound
                    publishToken.expectHeader().location(authConfig.loginRedirectUri)
                }
            }
        }
        When("로그아웃을 시도하면") {
            val logout = client.mutateWith(SecurityMockServerConfigurers.csrf()).post().uri("/oauth2/logout").exchange()
            Then("쿠키 만료") {
                logout.expectCookie().maxAge(authConfig.authentication, Duration.ofSeconds(0))
                logout.expectCookie().httpOnly(authConfig.authentication, true)
                logout.expectCookie().secure(authConfig.authentication, true)
            }
            Then("logoutRedirectUri로 리다이렉트") {
                logout.expectStatus().isFound
                logout.expectHeader().location(authConfig.logoutRedirectUri)
            }
        }
    }
}) {
    companion object {
        private const val PROVIDER = "test-provider"
        private const val USER = "test-user"
        private const val TOKEN = "SUCCESS_TOKEN"

        @TestConfiguration
        class TestConfig {
            @Bean fun authorizationConfig(): AuthorizationConfig = AuthorizationConfig().apply {
                authentication = "Authentication"
                loginRedirectUri = "main.html"
                logoutRedirectUri = "login.html"
            }
            @Bean fun tokenConfig() = TokenConfig()
            @Bean fun tokenPublisher() = mockkClass(PublishToken::class).apply {
                val token = TOKEN
                every { publish(any(), any()) } returns Mono.just(token)
            }
        }
        @JvmStatic
        private val oauthServer = GenericContainer(DockerImageName.parse("ghcr.io/navikt/mock-oauth2-server:2.1.4")).withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/$USER/.well-known/openid-configuration").forStatusCode(200))
        init {
            oauthServer.start()
        }
        @JvmStatic
        @DynamicPropertySource
        @Suppress("HttpUrlsUsage")
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            val baseUrl = "${oauthServer.host}:${oauthServer.getMappedPort(8080)}"
            registry.add("spring.security.oauth2.client.registration.$PROVIDER.client-id") { "test-client" }
            registry.add("spring.security.oauth2.client.registration.$PROVIDER.client-secret") { "test-secret" }
            registry.add("spring.security.oauth2.client.registration.$PROVIDER.scope") { "openid" }
            registry.add("spring.security.oauth2.client.registration.$PROVIDER.authorization-grant-type") { "authorization_code" }
            registry.add("spring.security.oauth2.client.registration.$PROVIDER.redirect-uri") { "{baseUrl}/login/oauth2/code/{registrationId}" }
            registry.add("spring.security.oauth2.client.provider.$PROVIDER.authorization-uri") { "http://$baseUrl/$USER/authorize" }
            registry.add("spring.security.oauth2.client.provider.$PROVIDER.token-uri") { "http://$baseUrl/$USER/token" }
            registry.add("spring.security.oauth2.client.provider.$PROVIDER.jwk-set-uri") { "http://$baseUrl/$USER/jwks" }
        }
    }
}