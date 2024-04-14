package net.sayaya

import net.sayaya.login.PublishToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI

@Configuration
class SecurityConfig(
    private val config: AuthorizationConfig,
    private val toeknPublisher: PublishToken,
    private val tokenConfig: TokenConfig,
) {
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        httpBasic { disable() }
        formLogin { disable() }
        headers { frameOptions { mode = XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN } }
        oauth2Login {
            securityContextRepository = NoOpServerSecurityContextRepository.getInstance()
            authenticationSuccessHandler = ServerAuthenticationSuccessHandler { filter, authentication ->
                val exchange = filter.exchange
                val provider = exchange.request.path.value().split("/").last()
                val principal = authentication.principal as OAuth2User
                toeknPublisher.publish(provider, principal).flatMap { token -> exchange.sendAuthenticationCookie(token) }
            }
        }
        logout {
            logoutUrl = "/oauth2/logout"
            logoutHandler = DelegatingServerLogoutHandler(SecurityContextServerLogoutHandler(), WebSessionServerLogoutHandler())
            logoutSuccessHandler = ServerLogoutSuccessHandler { exchange, _ ->
                exchange.exchange.clearAuthenticationCookie()
            }
        }
        authorizeExchange { authorize (anyExchange, permitAll) }
        exceptionHandling {
            authenticationEntryPoint = ServerAuthenticationEntryPoint { exchange, e ->
                e.printStackTrace()
                Mono.fromRunnable { exchange.response.statusCode = HttpStatus.UNAUTHORIZED } }
            accessDeniedHandler = ServerAccessDeniedHandler { exchange, _ -> Mono.fromRunnable { exchange.response.statusCode = HttpStatus.FORBIDDEN } }
        }
    }
    private fun ServerWebExchange.sendAuthenticationCookie(token: String): Mono<Void> {
        response.addCookie(ResponseCookie.from(config.authentication, token).path("/").httpOnly(true).secure(true).maxAge(tokenConfig.duration).build())
        response.statusCode = HttpStatus.MOVED_PERMANENTLY
        response.headers.location = URI.create(config.loginRedirectUri)
        return response.setComplete()
    }
    private fun ServerWebExchange.clearAuthenticationCookie(): Mono<Void> {
        response.addCookie(ResponseCookie.from(config.authentication).httpOnly(true).secure(true).maxAge(0).build())
        response.statusCode = HttpStatus.MOVED_PERMANENTLY
        response.headers.location = URI.create(config.logoutRedirectUri)
        return response.setComplete()
    }
}