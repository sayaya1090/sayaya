package net.sayaya.github

import net.sayaya.api.SecretRepository
import net.sayaya.client.data.GithubAppConfig
import net.sayaya.client.data.GithubConfigRequest
import net.sayaya.client.data.GithubRepositories
import net.sayaya.client.data.GithubRepositoryConfig
import net.sayaya.post.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service("net.sayaya.github.Handler")
class Handler (
    private val githubApi: GithubApi,
    private val userRepo: UserRepository,
    private val secretRepo: SecretRepository
) {
    fun findGithubRepoConfig(userId: UUID): Mono<GithubRepositoryConfig> = userRepo
        .findById(userId)
        .mapNotNull { it.github }
        .flatMap { principal -> secretRepo.findSecretByProviderAndAccount(provider = "github", principal!!, "app_id", "private_key", "installation_id", "owner", "repo", "branch") }
        .mapNotNull { secrets ->
            val appId = secrets["app_id"]
            val privateKey = secrets["private_key"]
            val installationId = secrets["installation_id"]
            val owner = secrets["owner"]
            val repo = secrets["repo"]
            val branch = secrets["branch"]
            val hasAuth = appId != null && privateKey != null && installationId != null
            GithubRepositoryConfig().auth(hasAuth).owner(owner).repo(repo).branch(branch)
        }
    fun findGithubAppConfigFromUserSecret(userId: UUID): Mono<GithubAppConfig> = userRepo.findById(userId)
        .mapNotNull { it.github }
        .flatMap { principal ->
            secretRepo.findSecretByProviderAndAccount(
                provider = "github",
                principal!!,
                "app_id",
                "private_key",
                "installation_id"
            )
        }.mapNotNull { secrets ->
            val appId = secrets["app_id"]
            val privateKey = secrets["private_key"]
            val installationId = secrets["installation_id"]
            if (appId == null || privateKey == null || installationId == null) null
            else GithubAppConfig().appId(appId).privateKey(privateKey).installationId(installationId)
        }
    fun saveGithubAppConfig(userId: UUID, dest: GithubConfigRequest): Mono<GithubRepositoryConfig> = userRepo.findById(userId).flatMap {
        checkNotNull(it.github)
        secretRepo.saveSecretByProviderAndAccount(
            provider = "github",
            account = it.github,
            data = mapOf(
                "app_id" to dest.appId,
                "private_key" to dest.privateKey,
                "installation_id" to dest.installationId,
                "owner" to dest.owner,
                "repo" to dest.repo,
                "branch" to dest.branch
            )
        ).then(Mono.defer { Mono.just(GithubRepositoryConfig().auth(true).owner(dest.owner).repo(dest.repo).branch(dest.branch)) })
    }
    fun repositories(githubAppConfig: GithubAppConfig): Mono<GithubRepositories> {
        val appId = githubAppConfig.appId
        val privateKey = githubAppConfig.privateKey
        val installId = githubAppConfig.installationId
        return with(githubApi) {
            app(appId, privateKey).
            flatMap { repositories(it, installId) }.
            map { response ->
                val owner = response.repositories.first().owner!!.login!!
                GithubRepositories().owner(owner).repos(response.repositories.mapNotNull(GithubApi.RepoResponse::name))
            }
        }
    }
    private fun GithubRepositories.repos(list: List<String>): GithubRepositories {
        this.repos = list.toTypedArray()
        return this;
    }
    fun branches(githubConfigRequest: GithubConfigRequest): Flux<String> {
        val appId = githubConfigRequest.appId
        val privateKey = githubConfigRequest.privateKey
        val installationId = githubConfigRequest.installationId
        val owner = githubConfigRequest.owner
        val repo = githubConfigRequest.repo
        return with(githubApi) {
            app(appId, privateKey).
            map(GithubApi.AppResponse::token).
            flatMap { jwt -> authorize(jwt, installationId) }.
            flatMapMany { token -> branches(owner, repo, token ) }.
            mapNotNull { it.name }
        }
    }
}