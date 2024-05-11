package net.sayaya.authentication

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository: R2dbcRepository<User, UUID>