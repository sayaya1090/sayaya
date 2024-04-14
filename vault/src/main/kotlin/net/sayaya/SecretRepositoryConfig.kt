package net.sayaya

import net.sayaya.api.SecretRepository
import net.sayaya.api.Vault
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.net.URI

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="spring.cloud.vault")
class VaultConfig {
    lateinit var uri: URI
    lateinit var token: String
    lateinit var kv: KvConfig
}
class KvConfig {
    lateinit var backend: String
}

@Configuration
class VaultSecretRepositoryConfig (private val config: VaultConfig) {
    @Bean
    fun secretRepository(): SecretRepository = Vault(config.uri, config.token, config.kv.backend)
}