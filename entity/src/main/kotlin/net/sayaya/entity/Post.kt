package net.sayaya.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "post", indexes = [
    Index(columnList = "author, title", unique = true),
    Index(columnList = "title"),
    Index(columnList = "description"),
    Index(columnList = "published_at"),
])
internal class Post {
    @Id lateinit var id: UUID
    @CreatedDate @Column(name = "created_at", nullable = false) private lateinit var createDateTime: LocalDateTime
    @LastModifiedDate @Column(name = "updated_at", nullable = false) private lateinit var lastModifyDateTime: LocalDateTime
    @Column(columnDefinition = "text", nullable = false) lateinit var title: String
    @JoinColumn(name="author", nullable = false) @ManyToOne lateinit var author: User
    @Column(columnDefinition = "text", nullable = false) lateinit var markdown: String
    @Column(columnDefinition = "text", nullable = false) lateinit var html: String
    @Column(name = "thumbnail_url", columnDefinition = "text") var thumbnailUrl: String? = null
    @Column(name = "github_url", columnDefinition = "text") var githubUrl: String? = null
    @Column(name = "description", columnDefinition = "text") var description: String? = null
    @Column(name = "published_at") private var publishedDateTime: LocalDateTime? = null
    @ElementCollection @Column(name = "keyword", columnDefinition = "text", nullable = false)
    @CollectionTable(name="post_keyword", joinColumns =[JoinColumn(name="post")], indexes = [
        Index(columnList = "post"),
        Index(columnList = "keyword")
    ])
    private lateinit var keywords: Set<String>
}