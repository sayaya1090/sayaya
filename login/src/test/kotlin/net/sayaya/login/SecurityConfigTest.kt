package net.sayaya.login

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import net.sayaya.SecurityConfig
import net.sayaya.authentication.*
import net.sayaya.login.testcontainers.OAuthServer
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
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import java.time.Duration

@WebFluxTest
@ContextConfiguration(classes = [SecurityConfig::class, SecurityConfigTest.Companion.TestConfig::class])
@Testcontainers
internal class SecurityConfigTest(
    private val client: WebTestClient,
    private val authConfig: AuthenticationConfig,
    private val urlConfig: AuthenticationUrlConfig
): BehaviorSpec({
    Given("서버 기동") {
        When("로그인 URL을 요청하면") {
            val request = client.get().uri("/oauth2/authorization/${OAuthServer.PROVIDER}").exchange()
            val loginUrl by lazy { request.returnResult<Any>().responseHeaders.location }
            val session by lazy { request.returnResult<Any>().responseCookies["SESSION"]!!.first().value }
            Then("로그인 페이지 정보 제공") {
                request.expectStatus().isFound
                request.expectCookie().exists("SESSION")
                request.expectCookie().doesNotExist(authConfig.header)
                request.expectHeader().exists("location")
            }
            And("전달된 로그인 페이지에서 로그인 성공하면") {
                val authentication = WebClient.create().post().uri(loginUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(LinkedMultiValueMap<String, String>().apply {
                        set("username", OAuthServer.USER)
                    }).retrieve().toBodilessEntity().map { response ->
                        response.statusCode shouldBe HttpStatus.FOUND
                        response.headers.location
                    }.block()
                val publishToken = client.get().uri(checkNotNull(authentication)).cookie("SESSION", session).exchange()
                Then("토큰 발급") {
                    publishToken.expectCookie().valueEquals(authConfig.header, OAuthServer.TOKEN)
                    publishToken.expectCookie().httpOnly(authConfig.header, true)
                    publishToken.expectCookie().secure(authConfig.header, true)
                }
                Then("loginRedirectUri로 리다이렉트") {
                    publishToken.expectStatus().isFound
                    publishToken.expectHeader().location(urlConfig.loginRedirectUri)
                }
            }
        }
        When("로그아웃을 시도하면") {
            val logout = client.post().uri("/oauth2/logout").exchange()
            Then("쿠키 만료") {
                logout.expectCookie().maxAge(authConfig.header, Duration.ofSeconds(0))
                logout.expectCookie().httpOnly(authConfig.header, true)
                logout.expectCookie().secure(authConfig.header, true)
            }
            Then("logoutRedirectUri로 리다이렉트") {
                logout.expectStatus().isFound
                logout.expectHeader().location(urlConfig.logoutRedirectUri)
            }
        }
    }
}) {
    companion object {
        @TestConfiguration
        class TestConfig {
            @Bean fun authorizationConfig(): AuthenticationConfig = AuthenticationConfig().apply {
                header = "Authentication"
            }
            @Bean fun authorizationUrlConfig(): AuthenticationUrlConfig = AuthenticationUrlConfig().apply {
                loginRedirectUri = "main.html"
                logoutRedirectUri = "login.html"
            }
            @Bean fun tokenConfig() = TokenFactoryConfig()
            @Bean fun tokenPublisher() = mockkClass(PublishToken::class).apply {
                every { publish(any(), any()) } returns Mono.just(OAuthServer.TOKEN)
            }
            @Bean fun jwtAuthenticationManager(): JwtAuthenticationManager = mockk()
            @Bean fun jwtAuthenticationConverter(): JwtAuthenticationConverter = mockk<JwtAuthenticationConverter>().apply {
                every { convert(any()) } returns Mono.empty()
            }
        }
        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            OAuthServer.registerDynamicProperties(registry)
        }
    }
}