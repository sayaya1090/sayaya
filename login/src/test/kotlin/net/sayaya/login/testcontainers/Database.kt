package net.sayaya.login.testcontainers

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

internal class Database: ApplicationContextInitializer<ConfigurableApplicationContext> {
    companion object {
        private val postgres = PostgreSQLContainer("postgres:latest")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("password")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"), "/docker-entrypoint-initdb.d/init.sql")
        init {
            postgres.start()
        }
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { "r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}" }
            registry.add("spring.r2dbc.username") { postgres.username }
            registry.add("spring.r2dbc.password") { postgres.password }
        }
    }

    override fun initialize(context: ConfigurableApplicationContext) {
        
    }
}