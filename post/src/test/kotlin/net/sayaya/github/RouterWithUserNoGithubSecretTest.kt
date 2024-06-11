package net.sayaya.github

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import net.sayaya.JsonConfig
import net.sayaya.api.SecretRepository
import net.sayaya.client.data.GithubAppConfig
import net.sayaya.client.data.GithubRepositories
import net.sayaya.github.RouterWithUserNoGithubSecretTest.Companion.USER_ID
import net.sayaya.post.User
import net.sayaya.post.UserRepository
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@ContextConfiguration(classes = [ Router::class, Handler::class, JsonConfig::class, RouterWithUserNoGithubSecretTest.Companion.TestConfig::class ])
@WebFluxTest
@WithMockUser(username = USER_ID)
internal class RouterWithUserNoGithubSecretTest(client : WebTestClient): BehaviorSpec({
    extensions(SpringExtension)
    Given("깃헙 인증 정보가 없는 사용자 계정으로") {
        When("저장된 깃헙 인증 정보를 요청하면") {
            val request = client.get().uri("/post/destination")
            Then("No Content를 반환된다") {
                val exchange = request.exchange()
                exchange.expectStatus().isNoContent
                exchange.expectBody().isEmpty
            }
        }
        When("App 인증 정보 없이 리파지토리 목록을 요청하면") {
            val request = client.get().uri("/post/repositories")
            Then("Precondition required 를 반환된다") {
                val exchange = request.exchange()
                exchange.expectStatus().isEqualTo(HttpStatus.PRECONDITION_REQUIRED)
            }
        }
        When("Post로 인증 정보를 직접 전달하여 리파지토리 목록을 요청하면") {
            val request = client.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/post/repositories")
                .bodyValue(GithubAppConfig().appId(GITHUB_APP_ID).privateKey(GITHUB_APP_PRIVATE_KEY).installationId(GITHUB_APP_INSTALLATION_ID_POST))
            Then("리파지토리 목록을 반환한다") {
                val exchange = request.exchange()
                exchange.expectStatus().isOk
                val value = exchange.expectBody(GithubRepositories::class.java).returnResult().responseBody
                checkNotNull(value)
                value.owner shouldBeEqual GITHUB_USER_ID
                value.repos shouldContainAll listOf(GITHUB_REPO_UPDATABLE, GITHUB_REPO_READ_ONLY)
            }
        }
        When("브랜치 목록을 요청하면") {
            val request = client.get().uri("/post/repositories/$GITHUB_USER_ID/$GITHUB_REPO_UPDATABLE/branches")
            Then("Precondition required 를 반환된다") {
                val exchange = request.exchange()
                exchange.expectStatus().isEqualTo(HttpStatus.PRECONDITION_REQUIRED)
            }
        }
        When("Post로 인증 정보를 직접 전달하여 브랜치 목록을 요청하면") {
            val request = client.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/post/repositories/$GITHUB_USER_ID/$GITHUB_REPO_UPDATABLE/branches")
                .bodyValue(GithubAppConfig().appId(GITHUB_APP_ID).privateKey(GITHUB_APP_PRIVATE_KEY).installationId(GITHUB_APP_INSTALLATION_ID_POST))
            Then("브랜치 목록을 반환한다") {
                val exchange = request.exchange()
                exchange.expectStatus().isOk
                val value = exchange.expectBody(List::class.java).returnResult().responseBody
                checkNotNull(value)
                value shouldContainExactly listOf(GITHUB_BRANCH_MAIN, GITHUB_BRANCH_OTHER)
            }
        }
    }
}) {
    companion object {
        const val USER_ID = "a293ab55-a402-4d75-9185-36566e5f9650"
        const val GITHUB_PRINCIPAL = "2222"
        const val GITHUB_USER_ID = "test-user"
        const val GITHUB_APP_ID = "test-app-id"
        const val GITHUB_APP_PRIVATE_KEY = "test-app-private-key"
        const val GITHUB_APP_INSTALLATION_ID_POST = "test-app-installation-id-post"
        const val GITHUB_REPO_UPDATABLE = "test-repo-updatable"
        const val GITHUB_REPO_READ_ONLY = "test-repo-read-only"
        const val GITHUB_BRANCH_MAIN = "main"
        const val GITHUB_BRANCH_OTHER = "other"
        const val GITHUB_TOKEN = "test-token"
        val user = User(UUID.fromString(USER_ID), "", GITHUB_PRINCIPAL)
        @TestConfiguration
        class TestConfig {
            @Bean fun github(): GithubApi = mockk<GithubApi>().apply {
                every { app(any(), any()) }.returns(Mono.just(mockk<GithubApi.AppResponse>().apply {
                    every { token }.answers { GITHUB_TOKEN }
                }))
                every { repositories(any(), any()) }.answers { answer ->
                    val installationId = answer.invocation.args[1] as String
                    val response = when (installationId) {
                        GITHUB_APP_INSTALLATION_ID_POST -> GithubApi.ReposResponse(
                            repositories = listOf(
                                GithubApi.RepoResponse(name=GITHUB_REPO_UPDATABLE, owner=GithubApi.OwnerResponse(login=GITHUB_USER_ID, reposUrl="URL1")),
                                GithubApi.RepoResponse(name=GITHUB_REPO_READ_ONLY, owner=GithubApi.OwnerResponse(login=GITHUB_USER_ID, reposUrl="URL2"))),
                            token = "")
                        else -> null
                    }
                    Mono.justOrEmpty(response)
                }
                every { authorize(any(), any()) }.answers { Mono.just(GITHUB_TOKEN) }
                every { branches(any(), any(), any()) }.returns(
                    Flux.just(
                    GithubApi.BranchesResponse(name = GITHUB_BRANCH_MAIN, commit = mockk()),
                    GithubApi.BranchesResponse(name = GITHUB_BRANCH_OTHER, commit = mockk())
                ))
            }
            @Bean fun secretRepository(): SecretRepository = mockk<SecretRepository>().apply {
                every { findSecretByProviderAndAccount(any(), any(), *anyVararg()) }.
                returns(Mono.empty())
            }
            @Bean fun userRepository(): UserRepository = mockk<UserRepository>().apply {
                every { findById(ofType(UUID::class)) }.answers { answer ->
                    Mono.justOrEmpty(
                        when (answer.invocation.args[0]) {
                            UUID.fromString(USER_ID) -> user
                            else -> null
                        }
                    )
                }
            }
        }
    }
}