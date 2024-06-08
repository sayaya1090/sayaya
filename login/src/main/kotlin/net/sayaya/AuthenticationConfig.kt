package net.sayaya

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="spring.security.authentication")
class AuthenticationConfig {
    lateinit var header: String
    lateinit var loginRedirectUri: String
    lateinit var logoutRedirectUri: String
}