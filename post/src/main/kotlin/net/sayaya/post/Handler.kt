package net.sayaya.post

import net.sayaya.ServerConfig
import net.sayaya.client.data.*
import net.sayaya.client.data.Post
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Stream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.streams.asStream

@Service("net.sayaya.post.Handler")
class Handler (
    private val repo: PostRepository,
    private val s3: S3,
    private val github: net.sayaya.github.Handler,
    private val serverConfig: ServerConfig
) {
   @Transactional
    fun post(userId: String, request: PostRequest): Mono<UUID> {
        val createEntity = Mono.defer { Mono.just(createEntity(LocalDateTime.now(), request)) }
        val entity = request.post.id?.let { repo.find(UUID.fromString(request.post.id)).switchIfEmpty(createEntity) } ?: createEntity
        return entity.flatMap { e ->
            e.toS3Path(userId).
            let { path -> request.uploadBase64ImageAndReplaceToS3Url(path) }.
            mayUploadToGitHub(UUID.fromString(userId), e.createdAt.format(dateTimeFormatter)).
            copyToEntity(e)
        }.flatMap { repo.save(it, request.catalog?.tags?.toList() ) }.map { it.id }
    }
    private fun createEntity(timestamp: LocalDateTime, request: PostRequest): net.sayaya.post.Post = Post (
        _id = UUID.randomUUID(),
        createdAt = timestamp,
        title = request.post.title,
        html = request.post.html,
        markdown = request.post.markdown,
        githubUrl = request.commit?.let {"https://${request.commit.destination.owner}.github.io/${request.commit.destination.repo}/${dateTimeFormatter.format(timestamp)}" }
    )
    private fun net.sayaya.post.Post.toS3Path(userId: String) = "files/$userId/${id}"
    private fun PostRequest.uploadBase64ImageAndReplaceToS3Url(path: String): Mono<PostRequest> {
        var html = this.post.html
        return Flux.fromArray(this.post.images?: emptyArray()).parallel().flatMap { image ->
            image.uploadToS3("$path/${image.name}")
        }.sequential().map { it.apply {
            html = replaceBase64ImageToS3Url(html, it)
        } }.collectList().map { Post()
            .id(this.post.id)
            .title(this.post.title)
            .markdown(this.post.markdown)
            .html(html)
            .images(it.toTypedArray())
        }.map { PostRequest().post(it).commit(this.commit) }
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun Image.uploadToS3(key: String): Mono<Image> = if(base64 != null) {
        val match = base64ImageFormat.find(base64)
        if(match!=null) {
            val contentType = match.groupValues[1]
            val decode = Base64.decode(match.groupValues[2])
            s3.upload(key, decode, "public-read", contentType).thenReturn (
                Image().id(id).name(name).url(serverConfig.externalUrl.resolve(key).toString()).base64(null).apply {
                    base64Decoded=match.groupValues[2]
                }
            )
        } else Mono.empty()
    } else Mono.empty()
    private fun replaceBase64ImageToS3Url(html: String, image: Image): String {
        val origin = "<img.* (src=\"\\s*${image.name}\\s*\").*>".toRegex()
        var tmp = html
        for (match in origin.findAll(html)) {
            val imgOrigin = match.groupValues[0]
            val imgReplacement = imgOrigin.replace(match.groupValues[1], "src=\"${image.url}\"")
            tmp = tmp.replace(imgOrigin, imgReplacement)
        }
        return tmp
    }
    private fun Commit?.uploadToGitHub(userId: UUID, files: List<Triple<String, String, String>>): Mono<Void> {
        if(this==null) return Mono.empty()
        return github.findGithubAppConfigFromUserSecret(userId).
        flatMap { auth -> github.commit(auth, destination.owner, destination.repo, destination.branch, msg, files) }
    }
    private fun Mono<PostRequest>.mayUploadToGitHub(userId: UUID, dir: String): Mono<PostRequest> = flatMap {
        it.commit.uploadToGitHub(userId, it.toCommitFiles(dir)).then(this)
    }
    private fun Mono<PostRequest>.copyToEntity(entity: net.sayaya.post.Post) = map { entity.apply {
        title = it.post.title
        html = it.post.html
        markdown = it.post.markdown
        description = it.catalog?.description
        // thumbnail = it.catalog?.thumbnail
    } }
    private fun PostRequest.toCommitFiles(dir: String): List<Triple<String, String, String>> {
        return Stream.concat(
            (post.images ?: emptyArray()).mapNotNull { image -> image.base64Decoded?.let { Triple(it, "$dir/${image.name}", "base64") } }.stream(),
            listOf(
                Triple(post.markdown, "$dir/README.md", "utf-8"),
                Triple("""
                    ---
                    title: ${post.title}
                    ${catalog?.publishedAt?.let { "publishedAt: $it" } ?: ""}
                    ${catalog?.description?.let { "description: $it" } ?: ""}
                    ${if (catalog?.tags?.isNotEmpty() == true) "tags: ${catalog.tags.joinToString(",")}" else ""}
                    ---
                """.trimIndent(),
                "$dir/meta", "utf-8")
            ).stream()
        ).toList()
    }
    @Transactional
    fun patch(userId: String, id: UUID, catalog: CatalogItem): Mono<UUID> = repo.find(id)
        .map{ e ->
            // e.toS3Path(userId)
            copyToEntity(catalog, e)
        }.flatMap { e -> repo.save(e, catalog.tags?.toList()) }
        .map { it.id }
    private fun copyToEntity(meta: CatalogItem, entity: net.sayaya.post.Post) = entity.apply {
        title = meta.title
        description = meta.description
        thumbnail = meta.thumbnail
        publishedAt = if(meta.published) LocalDateTime.now() else null
    }

    fun find(userId: String, id: UUID): Mono<Post> = repo.find(id).zipWith(repo.findKeyword(id)).flatMap {
        val (post, keywords) = it.t1 to it.t2
        val path = post.toS3Path(userId)
        val md = post.markdown
        Flux.fromStream(imageTagFormat.toRegex().findAll(md).asStream()).flatMap { match ->
            val id = match.groupValues[1]
            val name = match.groupValues[2]
            val extension = name.replace("$id.", "")
            downloadFromS3("$path/$name").map { base64 -> Image().id(id).name(name).url(null).base64("data:image/$extension;base64,$base64") }
        }.collectList().map { images ->
            Post().id(post.id.toString())
                .title(post.title)
                .markdown(post.markdown)
                .html(post.html)
                .images(images.toTypedArray())
        }
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun downloadFromS3(key: String): Mono<String> = s3.download(key).map(Base64::encode)

    companion object {
        private val base64ImageFormat = "data:(image/\\w+);base64,(.+)".toRegex()
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        private val imageTagFormat = "!\\[\\s*(image-\\d+)\\s*]\\(\\s*(image-\\d+[.]\\w+)\\s*\\)";
    }
}