package net.sayaya.blog

import net.sayaya.client.data.Article
import net.sayaya.client.data.CatalogItem
import net.sayaya.data.Search
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class Handler(val repo: Repository) {
    fun search(param: Search): Mono<Page<CatalogItem>> = repo.search(param).map { page -> PageImpl(page.content.map { it.toCatalog() }, page.pageable, page.totalElements) }
    fun find(id: String, title: String): Mono<Article> = repo.findByUserAndTitle(id, title)

    private fun Catalog.toCatalog(): CatalogItem = CatalogItem()
        .id(this.id.toString())
        .title(this.title)
        .tags(this.tags)
        .description(this.description)
        .thumbnail(this.thumbnail)
        .createdAt(this.createdAt)
        .author(this.authorAlias)
        .updatedAt(this.updatedAt)
        .publishedAt(this.publishedAt.toDouble())
        .published(true)
}