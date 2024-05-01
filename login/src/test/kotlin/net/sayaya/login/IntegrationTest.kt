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
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
internal class IntegrationTest(private val client: WebTestClient, private val db: DatabaseClient): BehaviorSpec({
    Given("서버 기동") {
        When("새로운 사용자로 OAuth2 로그인 요청하면") {
            val publishToken = client.login(USER)
            Then("쿠키로 JWT 토큰이 발급된다") {
                publishToken.expectCookie().exists(AUTHENTICATION)
                publishToken.expectCookie().httpOnly(AUTHENTICATION, true)
                publishToken.expectCookie().secure(AUTHENTICATION, true)
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
                publishToken.expectHeader().location("index.html")
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
            registry.add("spring.security.authentication.header") { AUTHENTICATION }
            registry.add("spring.security.authentication.login-redirect-uri") { "index.html" }
            registry.add("spring.security.authentication.logout-redirect-uri") { "login.html" }
            registry.add("spring.security.authentication.jwt.signature-algorithm") { "RS256" }
            registry.add("spring.security.authentication.jwt.duration") { "3600" }
            registry.add("spring.security.authentication.jwt.publisher") { PUBLISHER }
            registry.add("spring.security.authentication.jwt.client") { CLIENT }
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