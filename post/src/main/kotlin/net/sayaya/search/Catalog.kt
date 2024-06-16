package net.sayaya.search

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("catalog")
class Catalog (
    @Id val id: UUID,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val author: UUID,
    val authorAlias: String?,
    val title: String,
    val githubUrl: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val tags: Set<String>,
    val publishedAt: LocalDateTime?
)