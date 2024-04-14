package net.sayaya.login

import net.sayaya.api.SecretRepository
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime
import java.util.*

@Service
class PublishToken(
    private val factory: TokenFactory,
    private val secretRepository: SecretRepository,
    private val userRepository: UserRepository
) {
    fun publish(provider: String, principal: OAuth2User): Mono<String> = findUser(provider, principal)
        .switchIfEmpty{ createUser(provider, principal) }
        .flatMap(::userToToken)

    private fun findUser(provider: String, principal: OAuth2User): Mono<User> = secretRepository
        .findSecretByProviderAndAccount(provider, principal.name, "id").mapNotNull { it["id"] }
        .map(UUID::fromString)
        .flatMap(userRepository::findById)

    private fun createUser(provider: String, principal: OAuth2User): Mono<User> {
        val userId = UUID.randomUUID()
        val values = mutableMapOf<String, String>()
        if(principal.attributes["email"]!=null) values["email"] = principal.attributes["email"].toString()
        val user = User(id=userId/*, state = User.Companion.State.ACTIVATED, roles = emptyList()*/).apply {
            if("github".contentEquals(provider))        github = principal.name
            else if("google".contentEquals(provider))   google = principal.name
        }
        return secretRepository.createSecretByProviderAndAccount(provider, principal.name, userId, values)
            .then(userRepository.save(user))
    }
    private fun userToToken(user: User): Mono<String> = userRepository.save(user.apply { lastLoginDateTime = LocalDateTime.now() }).map(factory::publish)
}