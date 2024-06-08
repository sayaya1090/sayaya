package net.sayaya.post

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("public.user")
data class User(
    @Id private val id: UUID,
    val alias: String,
    val github: String?
)