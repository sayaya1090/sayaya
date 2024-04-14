package net.sayaya.login

internal object UserToToken {
    fun map(nbf: Long, exp: Long, iss: String, aud: String, iat: Long, user: User): Token {
        return Token(nbf, exp, iss, aud, iat, listOf("ROLE_USER"), user.id.toString())
    }
}