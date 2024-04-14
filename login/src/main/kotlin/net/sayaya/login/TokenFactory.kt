package net.sayaya.login

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SecureDigestAlgorithm
import net.sayaya.TokenConfig
import org.springframework.stereotype.Service
import java.security.PrivateKey
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class TokenFactory (
    private val config: TokenConfig,
    private val objectMapper: ObjectMapper,
    keyPair: KeyPair
) {
    private val privateKey = keyPair.private
    fun publish(user: User): String {
        val iat = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        val payload: Token = UserToToken.map(iat, iat + config.duration, config.publisher, config.client, iat, user)
        return sign(payload)
    }
    private fun sign(payload: Token): String {
        val signatureAlgorithm = Jwts.SIG.get()[config.signatureAlgorithm]
        if(signatureAlgorithm is SecureDigestAlgorithm) {
            return Jwts.builder()
                .header().add("typ", "JWT").and()
                .content(objectMapper.writeValueAsString(payload))
                .signWith(privateKey, signatureAlgorithm as SecureDigestAlgorithm<PrivateKey, *>)
                .compact()
        } else throw IllegalArgumentException("Unsupported algorithm: ${config.signatureAlgorithm}")
    }
}