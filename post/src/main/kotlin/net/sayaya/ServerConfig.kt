package net.sayaya

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.net.URI

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="server")
class ServerConfig {
    lateinit var externalUrl: URI
}