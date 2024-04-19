package net.sayaya.api

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContainValue
import io.kotest.matchers.shouldBe
import org.testcontainers.vault.VaultContainer
import java.net.URI
import java.util.*

class VaultTest: BehaviorSpec({
    val token = UUID.randomUUID().toString()
    val backend = "secret"
    val vault: VaultContainer<*> = VaultContainer("vault:1.13.2").withVaultToken(token)
    vault.start()
    Given("Vault와 연결된 다음") {
        val api = Vault(URI(vault.httpHostAddress), token, backend)
        When("존재하지 않는 사용자를 조회하면") {
            val request = api.findSecretByProviderAndAccount("any", "any", "id")
            Then("null을 리턴한다") { request.block() shouldBe null }
        }
        When("새로운 사용자를 저장하고 나면") {
            val provider = "provider"
            val principal = "principal"
            val id = UUID.randomUUID()
            val request = api.createSecretByProviderAndAccount(provider, principal, id)
            Then("저장된 사용자를 읽어올 수 있다") {
                request.block()
                val updated = api.findSecretByProviderAndAccount(provider, principal, "id").block()!!
                updated shouldContainValue id.toString()
            }
            When("시크릿을 추가하여 저장하면") {
                val addendum = "value"
                val request2 = api.saveSecretByProviderAndAccount(provider, principal, "key", addendum)
                Then("저장된 정보를 읽어올 수 있다") {
                    request2.block()
                    val updated = api.findSecretByProviderAndAccount(provider, principal, "key").block()!!
                    updated shouldContainValue addendum
                }
            }
        }
    }
})