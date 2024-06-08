package net.sayaya.github

import net.sayaya.api.SecretRepository
import net.sayaya.post.UserRepository
import org.springframework.stereotype.Service

@Service
class GithubAuthHandler(
    private val userRepo: UserRepository,
    private val secretRepo: SecretRepository
) {
}