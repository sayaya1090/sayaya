package net.sayaya.github

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.sayaya.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI
import java.util.*

@SpringBootTest(classes = [JsonConfig::class, WebConfig::class])
class GithubApiTest(private val webClient: WebClient): BehaviorSpec({
    val instance = GithubApi(GithubConfig().apply {
        uri = URI(System.getenv("_GITHUB_URL"))
        authorization = AuthorizationConfig().apply {
            jwt = JwtConfig().apply {
                signatureAlgorithm = "RS256"
                duration = 120
            }
        }
    }, client = webClient)
    val appId = System.getenv("_GITHUB_APP_ID")
    val installId = System.getenv("_GITHUB_INSTALL_ID")
    val privateKey = System.getenv("_GITHUB_PRIVATE_KEY")
    val repository = System.getenv("_GITHUB_TEST_REPOSITORY")
    val branch = System.getenv("_GITHUB_TEST_BRANCH")
    Given("인증 토큰 없이") {
        When("유효한 어플리케이션 토큰으로 인증을 시도하면") {
            val info = with(instance) { app(appId, privateKey) }.block()
            Then("식별 정보가 반환된다") {
                info shouldNotBe null
                info!!.id shouldBe appId
            }
        }
    }
    Given("인증이 된 상태에서") {
        val info = with(instance) { app(appId, privateKey) }.block()
        checkNotNull(info)
        When("리파지토리 목록을 요청하면") {
            val list = with(instance) { repositories(info, installId) }.map { c->c.repositories }.block()
            Then("접근 가능한 리파지토리 목록이 반환된다") {
                list shouldNotBe null
                list!!.size shouldBeGreaterThan 0
            }
            checkNotNull(list)
            When("특정 리파지토리의 메인 브랜치를 요청하면") {
                val repo = list.first { it.name == repository }
                val main = with(instance) { branch(repo.owner!!.login!!, repo.name!!, repo.token, branch) }.block()
                Then("메인 브랜치 정보가 반환된다") {
                    main shouldNotBe null
                }
                When("메인 브랜치에 텍스트를 업로드하면") {
                    val blob = with(instance) { upload(repo.owner!!.login!!, repo.name!!, repo.token, "Content of the blob", "utf-8") }.block()
                    Then("blob 응답이 반환된다") {
                        blob shouldNotBe null
                    }
                }
                val files = listOf(
                    Triple("ReadMe", "Readme.md", "utf-8"),
                    Triple("source", "src/main/kotlin/test.kt", "utf-8"),
                    Triple("resource", "src/main/resources/test.txt", "utf-8")
                )
                val commitMessage = "Test Commit"
                When("메인 브랜치에 여러 파일을 업로드하면") {
                    val trees = with(instance) { files.map { upload(repo.owner!!.login!!, repo.name!!, repo.token, it.first, it.third).block()!!.toTreeItem(it.second) } }
                    Then("blob 응답이 반환된다") {
                        trees shouldNotBe null
                        trees.stream().noneMatch(Objects::isNull) shouldBe true
                    }
                    checkNotNull(main)
                    When("blob을 트리로 변환하여 메인 브랜치에 업로드하면") {
                        val treeResponse = with(instance) { upload(main, main.toTree(trees)) }.block()
                        Then("트리 응답이 반환된다") {
                            treeResponse shouldNotBe null
                        }
                        checkNotNull(treeResponse)
                        When("리파지토리에 생성된 트리를 커밋하면") {
                            val commitResponse = with(instance) { commit(treeResponse, commitMessage) }.block()
                            Then("커밋 응답이 반환된다") {
                                commitResponse shouldNotBe null
                            }
                            checkNotNull(commitResponse)
                            When("브랜치의 Head Ref를 커밋 결과로 변경하면") {
                                val updateRefResponse = with(instance) { updateRef(commitResponse) }.block()
                                Then("업데이트 응답이 반환된다") {
                                    updateRefResponse shouldNotBe null
                                    updateRefResponse!!.obj.sha shouldBeEqual commitResponse.sha
                                }
                                When("메인 브랜치를 요청하면") {
                                    val updatedMain = with(instance) { branch(repo.owner!!.login!!, repo.name!!, repo.token, branch) }.block()
                                    Then("커밋한 정보로 트리가 변경되어 있다") {
                                        updatedMain!!.sha shouldBeEqual commitResponse.sha
                                    }
                                }
                            }
                        }
                    }
                }
                When("Push 함수로 한번에 커밋을 요청하면") {
                    val push = GithubApi.Push (
                        branch = branch,
                        files = files,
                        message = commitMessage
                    )
                    val updateRefResponse = with(instance) { commit(repo.owner!!.login!!, repo.name!!, repo.token, push) }.block()
                    Then("업데이트 응답이 반환된다") {
                        updateRefResponse shouldNotBe null
                    }
                    When("메인 브랜치를 요청하면") {
                        val updatedMain = with(instance) { branch(repo.owner!!.login!!, repo.name!!, repo.token, branch) }.block()
                        Then("커밋한 정보로 트리가 변경되어 있다") {
                            updatedMain!!.sha shouldBeEqual updateRefResponse!!.obj.sha
                        }
                    }
                }
            }
        }
    }
})