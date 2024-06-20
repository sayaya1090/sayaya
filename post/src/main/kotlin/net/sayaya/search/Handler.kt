package net.sayaya.search

import net.sayaya.client.data.CatalogItem
import net.sayaya.data.Search
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.ZoneOffset

@Service
class Handler(private val repo: Repository) {
    private fun map(post: Catalog): CatalogItem = CatalogItem()
        .id(post.id.toString())
        .title(post.title)
        .author(post.authorAlias)
        .createdAt(post.createdAt.toEpochSecond(ZoneOffset.UTC))
        .updatedAt(post.updatedAt.toEpochSecond(ZoneOffset.UTC))
        .description(post.description)
        .thumbnail(post.thumbnailUrl)
        .tags(post.tags.toTypedArray())
        .published(post.publishedAt!=null)
        .publishedAt(post.publishedAt?.toEpochSecond(ZoneOffset.UTC)?.toDouble())
        .url("/post#${post.id}")
    fun search(param: Search): Mono<Page<CatalogItem>> = repo.search(param).mapNotNull { page ->
        if(page.content.isEmpty()) null
        else PageImpl(page.content.map(::map), page.pageable, page.totalElements)
    }
}