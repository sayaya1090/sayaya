package net.sayaya.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "\"user\"", indexes = [Index(columnList = "alias", unique = true)])
internal class User {
    @Id lateinit var id: UUID
    @Column(length=64) var alias: String? = null
    @CreatedDate @Column(name = "created_at", nullable = false) private lateinit var createDateTime: LocalDateTime
    @LastModifiedDate @Column(name = "last_modified_at", nullable = false) private lateinit var lastModifyDateTime: LocalDateTime
    @Column(name = "last_login_at") private val lastLoginDateTime: LocalDateTime? = null
    @Column(length=12) var state: String? = null
    @Column(length=128) var roles: String? = null
    @Column(length=16) var github: String? = null
    @Column(length=24) var google: String? = null
}