package net.sayaya.api

import reactor.core.publisher.Mono
import java.util.*

interface SecretRepository {
    fun findSecretByProviderAndAccount(provider: String, account: String, vararg key: String): Mono<Map<String, String?>>
    fun saveSecretByProviderAndAccount(provider: String, account: String, key: String, value: String?): Mono<Void> = saveSecretByProviderAndAccount(provider, account, mapOf(key to value))
    fun saveSecretByProviderAndAccount(provider: String, account: String, data: Map<String, String?>): Mono<Void>
    fun createSecretByProviderAndAccount(provider: String, account: String, id: UUID, data: Map<String, String?> = emptyMap()): Mono<Void>
}