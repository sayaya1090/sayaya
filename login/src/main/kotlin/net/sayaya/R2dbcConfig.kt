package net.sayaya

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.spi.ConnectionFactory
import net.sayaya.login.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver

import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
class R2dbcConfig {
    class UserRoleConverter: Converter<String, List<User.Companion.Role>> {
        override fun convert(source: String): List<User.Companion.Role> {
            check(source.startsWith("{") && source.endsWith("}"))
            return source.removeSurrounding("{", "}").split(",").stream()
                .map(String::trim)
                .map(User.Companion.Role::valueOf).toList()
        }
    }
    @Bean
    fun r2dbcCustomConversions(connectionFactory: ConnectionFactory, objectMapper: ObjectMapper): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(connectionFactory)
        return R2dbcCustomConversions.of(dialect, UserRoleConverter())
    }
}