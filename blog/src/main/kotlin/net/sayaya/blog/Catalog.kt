package net.sayaya.blog

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("catalog")
class Catalog (
    @Id val id: UUID,
    val title: String,
    val tags: Array<String>,
    @Column("author_alias") val authorAlias: String,
    @Column("created_at") val createdAt: Long,
    @Column("updated_at") val updatedAt: Long,
    @Column("published_at") val publishedAt: Long,
    val description: String?,
    @Column("thumbnail_url") val thumbnail: String?,
    @Column("github_url") val url: String?,
    val published: Boolean?
)