package net.sayaya.authentication

internal object UserToToken {
    fun map(nbf: Long, exp: Long, iss: String, aud: String, iat: Long, user: User): Token {
        return Token(nbf, exp, iss, aud, iat, user.roles.map { "ROLE_$it" }, user.id.toString())
    }
}