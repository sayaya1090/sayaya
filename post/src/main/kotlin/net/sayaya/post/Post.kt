package net.sayaya.post

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("post")
class Post (
    @Id @Column("id") var _id: UUID,
    val createdAt: LocalDateTime,
    var title: String,
    var html: String,
    var markdown: String,
    val githubUrl: String?
): Persistable<UUID> {
    @LastModifiedDate lateinit var updatedAt: LocalDateTime
    @CreatedBy lateinit var author: UUID
    var description: String? = null
    @Column("thumbnail_url") var thumbnail: String? = null
    var publishedAt: LocalDateTime? = null
    override fun getId(): UUID = _id
    override fun isNew() = this::updatedAt.isInitialized.not()
}