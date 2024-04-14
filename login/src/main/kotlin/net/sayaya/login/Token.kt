package net.sayaya.login

import java.util.*

data class Token (
    val nbf: Long,
    val exp: Long,
    val iss: String,
    val aud: String,
    val iat: Long,
    val authorities: List<String>,
    val name: String,
    val id: String = UUID.randomUUID().toString()
)