package net.sayaya

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.net.URI

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="github")
class GithubConfig {
    lateinit var uri: URI
    var apiVersion: String = "2022-11-28"
    var authorization: AuthorizationConfig = AuthorizationConfig()
}
class AuthorizationConfig {
    var jwt: JwtConfig = JwtConfig()
}
class JwtConfig {
    var signatureAlgorithm: String = "RS256"
    var duration: Long = 120
}