package net.sayaya.post

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import software.amazon.awssdk.transfer.s3.S3TransferManager
import software.amazon.awssdk.transfer.s3.model.DownloadRequest

@Component
class S3 (
    private val s3: S3TransferManager,
    private val client: S3Client,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucket: String
) {
    fun upload(key: String, input: ByteArray, acl: String="public-read", contentType: String): Mono<PutObjectResponse> {
        val request = PutObjectRequest.builder().bucket(bucket).key(key).acl(acl).contentType(contentType).build()
        return Mono.just(client.putObject(request, RequestBody.fromBytes(input)))
    }
    fun download(key: String): Mono<ByteArray> {
        val request = DownloadRequest.builder()
            .getObjectRequest { req -> req.bucket(bucket).key(key) }
            .responseTransformer(AsyncResponseTransformer.toBlockingInputStream())
            .build()
        return s3.download(request).completionFuture().let { Mono.fromFuture(it) }.publishOn(Schedulers.boundedElastic()).map { it.result().readAllBytes() }
    }
}