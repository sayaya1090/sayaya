package net.sayaya

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="spring.security.authorization.jwt")
class TokenConfig {
    lateinit var signatureAlgorithm: String
    var duration: Long = -1L
    lateinit var publisher: String
    lateinit var client: String
    lateinit var secret: String
}