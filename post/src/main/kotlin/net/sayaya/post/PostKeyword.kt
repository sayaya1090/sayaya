package net.sayaya.post

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("post_keyword")
class PostKeyword (
    val post: UUID,
    val keyword: String
) {
    @Id @Transient
    val id: String = "$post:$keyword"
}