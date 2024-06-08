package net.sayaya.github

import com.fasterxml.jackson.annotation.JsonProperty
import net.sayaya.GithubConfig
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

@Component
class GithubApi(config: GithubConfig, private val client: WebClient) {
    private val uri: URI = config.uri
    private val apiVersion: String = config.apiVersion
    private val algorithm: String = config.authorization.jwt.signatureAlgorithm
    private val tokenDuration: Long = config.authorization.jwt.duration

    private fun <T : RequestHeadersSpec<T>> RequestHeadersSpec<T>.withAuthenticate (
        accept: String = "application/vnd.github+json",
        auth: (HttpHeaders) -> Unit
    ): T = this
        .headers(auth)
        .header("Accept", accept)
        .header("X-GitHub-Api-Version", apiVersion)

    fun user(token: String): Mono<String> = client.get()
        .uri(uri.resolve("user"))
        .withAuthenticate { it.setBearerAuth(token) }
        .exchangeToMonoWithError { it.bodyToMono(String::class.java) }
    fun app(appId: String, privateKey: String): Mono<AppResponse> = jwt(appId, privateKey).flatMap(::app)

    fun jwt(appId: String, privateKey: String): Mono<String> {
        val iat = System.currentTimeMillis() / 1000
        val exp = iat + tokenDuration
        val payload = "{\"iat\":$iat,\"exp\":$exp,\"iss\":$appId}"
        return Mono.just(JwtTokenFactory.generate(payload, algorithm, privateKey))
    }
    fun app(jwt: String): Mono<AppResponse> = client.get()
        .uri(uri.resolve("app"))
        .withAuthenticate { it.setBearerAuth(jwt) }
        .exchangeToMonoWithError { it.bodyToMono(AppResponse::class.java) }
        .map { it.apply { token = jwt } }

    fun authorize(jwt: String, installationId: String): Mono<String> = client.post()
        .uri(uri.resolve("/app/installations/$installationId/access_tokens"))
        .withAuthenticate { it.setBearerAuth(jwt) }
        .exchangeToMonoWithError { it.bodyToMono(TokenResponse::class.java) }
        .map { it.token }
        .onErrorComplete()

    fun repositories(app: AppResponse, installationId: String): Mono<ReposResponse> = authorize(app.token, installationId)
        .flatMap { token ->
            check(app.owner != null)
            check(app.owner.reposUrl != null)
            client.get()
                .uri(app.owner.reposUrl)
                .withAuthenticate { it.setBearerAuth(token) }
                .exchangeToFluxWithError { it.bodyToFlux(RepoResponse::class.java) }
                .map { repo->repo.apply { this.token = token } }
                .collectList()
                .map { list-> ReposResponse(list, token) }
        }
    fun branches(owner: String, repo: String, token: String): Flux<BranchesResponse> = client.get()
        .uri(uri.resolve("/repos/$owner/$repo/branches"))
        .withAuthenticate { it.setBearerAuth(token) }
        .exchangeToFluxWithError {it.bodyToFlux(BranchesResponse::class.java) }
        .map { it.apply {
            this.token = token
        } }
    fun branch(owner: String, repo: String, token: String, branchName: String): Mono<BranchResponse> = client.get()
        .uri(uri.resolve("/repos/$owner/$repo/git/trees/$branchName"))
        .withAuthenticate { it.setBearerAuth(token) }
        .exchangeToMonoWithError { it.bodyToMono(BranchResponse::class.java) }
        .map { it.apply {
            name = branchName
            this.token = token
        } }
    fun upload(owner: String, repo: String, token: String, content: String, encoding: String): Mono<BlobResponse> = client.post()
        .uri(uri.resolve("/repos/$owner/$repo/git/blobs"))
        .withAuthenticate { it.setBearerAuth(token) }
        .bodyValue(UploadContent(content, encoding))
        .exchangeToMonoWithError { it.bodyToMono(BlobResponse::class.java) }
    data class UploadContent(val content: String, val encoding: String = "utf-8")

    fun upload(branch: BranchResponse, tree: Tree): Mono<TreeResponse> = client.post()
        .uri(branch.url.replace("/${branch.sha}", ""))
        .withAuthenticate { it.setBearerAuth(branch.token) }
        .bodyValue(tree)
        .exchangeToMonoWithError { it.bodyToMono(TreeResponse::class.java) }
        .map { it.apply {
            parent = branch
            token = branch.token
            baseTree = branch.sha
        } }

    fun commit(tree: TreeResponse, msg: String): Mono<CommitResponse> = client.post()
        .uri(tree.url.replace("/trees/${tree.sha}", "/commits"))
        .withAuthenticate { it.setBearerAuth(tree.token) }
        .bodyValue(Commit(msg, tree.sha, listOf(tree.baseTree)))
        .exchangeToMonoWithError { it.bodyToMono(CommitResponse::class.java) }
        .map { it.apply {
            branch = tree.parent
            token = tree.token
        } }
    fun updateRef(commit: CommitResponse): Mono<UpdateRefResponse> = client.patch()
        .uri(commit.url.replace("/commits/${commit.sha}", "/refs/heads/${commit.branch.name}"))
        .withAuthenticate { it.setBearerAuth(commit.token) }
        .bodyValue("{\"sha\":\"${commit.sha}\",\"force\":true}")
        .exchangeToMonoWithError { it.bodyToMono(UpdateRefResponse::class.java) }

    fun commit(owner: String, repo: String, token: String, push: Push): Mono<UpdateRefResponse> {
        val treeItems = Flux.fromIterable(push.files)
            .flatMap { (content, path, encoding) ->
                upload(owner, repo, token, content, encoding).map { it.toTreeItem(path) }
            }.collectList()
        val branch = branch(owner, repo, token, push.branch)
        return branch.zipWith(treeItems)
            .flatMap { tuple -> upload(tuple.t1, tuple.t1.toTree(tuple.t2) ) }
            .flatMap { commit(it, push.message) }
            .flatMap { updateRef(it) }
    }

    private data class TokenResponse (
        val token: String,
    )
    data class AppResponse (
        val id: String?,
        val name: String?,
        val owner: OwnerResponse?,
    ) {
        lateinit var token: String
    }
    data class OwnerResponse (
        val login: String?,
        //val id: String?,
        val reposUrl: String?,
    )
    data class RepoResponse (
        //val id: String?,
        val name: String?,
        val owner: OwnerResponse?,
        //val homepage: String?,
        //val size: String?,
        //val language: String?,
        //val visibility: String?,
        //val permissions: PermissionResponse?
    ) {
        lateinit var token: String
    }
    data class ReposResponse (
        val repositories: List<RepoResponse>,
        val token: String
    )
    data class BranchesResponse (
        val commit: CommitResponse,
        val name: String
    ) {
        lateinit var token: String
    }
    data class BranchResponse (
        val sha: String,
        val url: String,
        val truncated: Boolean
    ) {
        lateinit var token: String
        lateinit var name: String
        fun toTree(treeItems: List<TreeItem>): Tree = Tree(treeItems, sha)
    }
    data class BlobResponse (
        val sha: String,
        val url: String,
    ) {
        fun toTreeItem(path: String, mode: String = "100644"): TreeItem = TreeItem(this, path, mode)
    }
    data class TreeItem (
        val path: String,
        val mode: String,
        val type: String,
        val sha: String
    ) {
        constructor(blob: BlobResponse, path: String, mode: String) : this(path, mode, "blob", blob.sha)
    }
    data class Tree(
        val tree: List<TreeItem>,
        val baseTree: String
    )
    data class TreeResponse (
        val sha: String,
        val url: String,
        val truncated: Boolean
    ) {
        lateinit var token: String
        lateinit var parent: BranchResponse
        lateinit var baseTree: String
    }
    data class Commit(
        val message: String,
        val tree: String,
        val parents: List<String>?
    )
    data class CommitResponse (
        val sha: String,
        val url: String,
    ) {
        lateinit var token: String
        lateinit var branch: BranchResponse
    }
    data class UpdateRefResponse (
        val ref: String,
        val url: String,
        @JsonProperty("object") val obj: CommitResponse
    )
    data class Push (
        val branch: String,
        val files: List<Triple<String, String, String>>,
        val message: String
    )
    private fun <T, F: Flux<T>> RequestHeadersSpec<*>.exchangeToFluxWithError(p: (ClientResponse) -> F): Flux<T> = exchangeToFlux {
        if (it.statusCode().is2xxSuccessful) p(it)
        else Flux.error(WebClientResponseException(it.statusCode().value(), "GitHub API Error", it.headers().asHttpHeaders(), null, null))
    }
    private fun <T, M: Mono<T>> RequestHeadersSpec<*>.exchangeToMonoWithError(p: (ClientResponse) -> M): Mono<T> = exchangeToMono {
        if (it.statusCode().is2xxSuccessful) p(it)
        else Mono.error(WebClientResponseException(it.statusCode().value(), "GitHub API Error", it.headers().asHttpHeaders(), null, null))
    }
}