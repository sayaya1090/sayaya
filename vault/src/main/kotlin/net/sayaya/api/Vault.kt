package net.sayaya.api

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient.RequestBodySpec
import org.springframework.web.client.RestClient.RequestHeadersSpec
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*

// Outbound adapter
class Vault (
    private val uri: URI,
    private val token: String,
    private val backend: String
): SecretRepository {
    private fun resolve(provider: String, principal: String) = uri.resolve("v1/$backend/data/authentication/$provider/$principal")
    private val client = WebClient.create()
    private fun findSecretByProviderAndAccount(provider: String, account: String): Mono<Map<String, String?>> = client.get()
        .uri(resolve(provider, account))
        .header("X-Vault-Token", token)
        .exchangeToMono { response ->
            if(response.statusCode() == HttpStatus.OK) response.bodyToMono(VaultKvResponse::class.java)
            else if(response.statusCode() == HttpStatus.NOT_FOUND) Mono.empty()
            else response.bodyToMono(String::class.java).flatMap { Mono.error(RuntimeException(it)) }
        }.retry(3)
        .map { it.data.data }
    private fun WebClient.RequestBodySpec.exchange(data: Map<String, String?>): Mono<Void> =
        bodyValue(VaultKvDataResponse(data = data))
            .header("X-Vault-Token", token)
            .exchangeToMono { response ->
                if(response.statusCode() == HttpStatus.OK) response.bodyToMono(VaultKvDataResponse::class.java)
                else response.bodyToMono(String::class.java).flatMap { Mono.error(RuntimeException(it)) }
            }.retry(3).then()
    override fun findSecretByProviderAndAccount(provider: String, account: String, vararg key: String): Mono<Map<String, String?>> = findSecretByProviderAndAccount(provider, account).map { data -> key.associateWith { data[it] } }
    override fun saveSecretByProviderAndAccount(provider: String, account: String, data: Map<String, String?>): Mono<Void> = client.patch()
        .uri(resolve(provider, account)).contentType(MediaType("application", "merge-patch+json"))
        .exchange(data)
    override fun createSecretByProviderAndAccount(provider: String, account: String, id: UUID, data: Map<String, String?>): Mono<Void> = client.post()
        .uri(resolve(provider, account))
        .exchange(mutableMapOf<String, String?>("id" to id.toString()).apply { putAll(data) })

    companion object {
        data class VaultKvResponse (
            val data: VaultKvDataResponse
        )
        data class VaultKvDataResponse (
            val data: Map<String, String?>
        )
    }
}