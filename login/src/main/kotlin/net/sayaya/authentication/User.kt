package net.sayaya.authentication

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("public.user")
data class User (
    @Id private val id: UUID,
    val roles: MutableList<Role> = mutableListOf(Role.USER)
): Persistable<UUID> {
    var alias: String? = null
    var github: String? = null
    var google: String? = null
    var state: State = State.ACTIVATED
    @CreatedDate @Column("created_at") lateinit var createDateTime: LocalDateTime
    @Column("last_login_at") var lastLoginDateTime: LocalDateTime = LocalDateTime.now()
    @LastModifiedDate @Column("last_modified_at") lateinit var lastModifyDateTime: LocalDateTime
    override fun getId(): UUID = id
    override fun isNew(): Boolean = this::createDateTime.isInitialized.not()
    companion object {
        enum class State {
            ACTIVATED, INACTIVATED,
        }
        enum class Role {
            USER, ADMIN
        }
    }
}