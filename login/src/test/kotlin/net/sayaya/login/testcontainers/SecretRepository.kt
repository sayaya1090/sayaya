package net.sayaya.login.testcontainers

import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import org.testcontainers.vault.VaultContainer
import java.util.*

internal class SecretRepository {
    companion object {
        private val token = UUID.randomUUID().toString()
        private val backend = "secret"
        private val vault = VaultContainer("vault:1.13.3").withVaultToken(token)
        init {
            vault.start()
        }
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.cloud.vault.uri") { vault.httpHostAddress }
            registry.add("spring.cloud.vault.token") { token }
            registry.add("spring.cloud.vault.kv.backend") { backend }
        }
    }
}