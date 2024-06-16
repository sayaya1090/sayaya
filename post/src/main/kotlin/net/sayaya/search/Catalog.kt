package net.sayaya.search

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("catalog")
class Catalog (
    @Id val id: UUID?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val author: UUID,
    @Column("author_alias") val authorAlias: String?,
    val title: String,
    val githubUrl: String?,
    val description: String?,
    @Column("thumbnail_url") val thumbnail: String?,
    val tags: Set<String>,
    val publishedAt: LocalDateTime?
)