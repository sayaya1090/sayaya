package net.sayaya.login

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.sayaya.login.testcontainers.Database
import net.sayaya.login.testcontainers.OAuthServer
import net.sayaya.login.testcontainers.OAuthServer.Companion.USER
import net.sayaya.login.testcontainers.SecretRepository
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@SpringBootTest(properties=[
    "spring.security.authentication.header=${ IntegrationTest.AUTHENTICATION }",
    "spring.security.authentication.login-redirect-uri=index.html",
    "spring.security.authentication.logout-redirect-uri=login.html",
    "spring.security.authentication.jwt.signature-algorithm=RS256",
    "spring.security.authentication.jwt.duration=3600",
    "spring.security.authentication.jwt.publisher=${ IntegrationTest.PUBLISHER }",
    "spring.security.authentication.jwt.client=${ IntegrationTest.CLIENT }"
])
@AutoConfigureWebTestClient
@Testcontainers
internal class IntegrationTest(private val client: WebTestClient, private val db: DatabaseClient): BehaviorSpec({
    Given("인증이 안 된 상태에서") {
        When("메뉴를 요청하면") {
            val request = client.get().uri("/menu").exchange()
            Then("로그인 메뉴를 출력한다") {
                request.expectStatus().isOk
                       .expectBody().jsonPath("$.title").isEqualTo("SIGN IN")
            }
        }
        When("새로운 사용자로 OAuth2 로그인 요청하면") {
            val publishToken = client.login(USER)
            Then("쿠키로 JWT 토큰이 발급된다") {
                publishToken.expectCookie().exists(AUTHENTICATION)
                            .expectCookie().httpOnly(AUTHENTICATION, true)
                            .expectCookie().secure(AUTHENTICATION, true)
                            .expectCookie().path(AUTHENTICATION, "/")
                            //.expectCookie().sameSite(AUTHENTICATION, "LAX")
            }
            Then("JWT 토큰을 decrypt 할 수 있고 그 값으로 원래 사용자의 권한 등 토큰 정보를 확인할 수 있다") {
                val token = publishToken.returnResult<Void>().responseCookies[AUTHENTICATION]!!.first().value
                val decrypt = TokenFactoryTest.jwtParser(TokenFactoryTest.PUBLIC_KEY).parseSignedClaims(token)
                val claims = decrypt.payload
                claims["authorities"] shouldNotBe null
                val authorities = claims["authorities"].shouldBeInstanceOf<List<String>>()
                authorities shouldContain "ROLE_USER"
                claims.issuer shouldBeEqual PUBLISHER
                claims.audience shouldContain CLIENT
                claims.notBefore shouldNotBe null
            }
            Then("loginRedirectUri로 리다이렉트 요청으로 응답한다") {
                publishToken.expectStatus().isFound
                            .expectHeader().location("index.html")
            }
            When("인증 쿠키와 함께 메뉴를 요청하면") {
                val token = publishToken.returnResult<Void>().responseCookies[AUTHENTICATION]!!.first().value
                val request = client.get().uri("/menu").cookie(AUTHENTICATION, token).exchange()
                Then("로그아웃 메뉴를 출력한다") {
                    request.expectStatus().isOk
                           .expectBody().jsonPath("$.title").isEqualTo("SIGN OUT")
                }
            }
            When("로그아웃을 시도하면") {
                val token = publishToken.returnResult<Void>().responseCookies[AUTHENTICATION]!!.first().value
                val logout = client.post().uri("/oauth2/logout").cookie(AUTHENTICATION, token).exchange()
                Then("쿠키 만료") {
                    logout.expectCookie().maxAge(AUTHENTICATION, Duration.ofSeconds(0))
                          .expectCookie().httpOnly(AUTHENTICATION, true)
                          .expectCookie().secure(AUTHENTICATION, true)
                          .expectCookie().path(AUTHENTICATION, "/")
                }
                Then("logoutRedirectUri로 리다이렉트") {
                    logout.expectStatus().isFound
                          .expectHeader().location("login.html")
                }
            }
        }
    }
    Given("Admin 계정 생성") {
        val publishToken = client.login("test-admin")
        val token = publishToken.returnResult<Void>().responseCookies[AUTHENTICATION]!!.first().value
        val decrypt = TokenFactoryTest.jwtParser(TokenFactoryTest.PUBLIC_KEY).parseSignedClaims(token)
        val claims = decrypt.payload
        val id = claims["name"]
        db.sql("UPDATE \"user\" SET roles='{USER,ADMIN}' WHERE id='$id'").fetch().rowsUpdated().block() shouldBe 1
        val userCnt = db.countUser()

        @Suppress("NAME_SHADOWING")
        When("기존에 존재하는 사용자(admin)로 OAuth2 로그인 요청하면") {
            val publishToken = client.login("test-admin")
            Then("JWT 토큰을 decrypt 하여 Admin 권한을 확인할 수 있다") {
                val token = publishToken.returnResult<Void>().responseCookies[AUTHENTICATION]!!.first().value
                val decrypt = TokenFactoryTest.jwtParser(TokenFactoryTest.PUBLIC_KEY).parseSignedClaims(token)
                val claims = decrypt.payload
                claims["authorities"] shouldNotBe null
                val authorities = claims["authorities"].shouldBeInstanceOf<List<String>>()
                authorities shouldContain "ROLE_ADMIN"
            }
            Then("사용자는 추가되지 않는다") {
                db.countUser() shouldBeEqual userCnt
            }
        }
    }
}) {
    companion object {
        const val AUTHENTICATION = "Authentication"
        const val PUBLISHER = "publisher.test"
        const val CLIENT = "client.test"
        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            Database.registerDynamicProperties(registry)
            OAuthServer.registerDynamicProperties(registry)
            SecretRepository.registerDynamicProperties(registry)
            registry.add("spring.security.authentication.jwt.secret") { TokenFactoryTest.PRIVATE_KEY }
        }
        fun DatabaseClient.countUser(): Long = sql("SELECT count(*) from \"user\"").fetch().one().mapNotNull { it.values.first() as Long }.block()!!
        fun WebTestClient.login(username: String): WebTestClient.ResponseSpec {
            val request = get().uri("/oauth2/authorization/${OAuthServer.PROVIDER}").exchange()
            val loginUrl by lazy { request.returnResult<Any>().responseHeaders.location }
            val session by lazy { request.returnResult<Any>().responseCookies["SESSION"]!!.first().value }
            val authentication = WebClient.create().post().uri(loginUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(LinkedMultiValueMap<String, String>().apply {
                    set("username", username)
                }).retrieve().toBodilessEntity().map { response ->
                    response.statusCode shouldBe HttpStatus.FOUND
                    response.headers.location
                }.block()
            return get().uri(checkNotNull(authentication)).cookie("SESSION", session).exchange()
        }
    }
}