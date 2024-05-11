package net.sayaya.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SecureDigestAlgorithm
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.util.encoders.Base64
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.RSAPrivateCrtKeySpec
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.regex.Pattern

@Service
class TokenFactory (
    tokenConfig: TokenConfig,
    private val config: TokenFactoryConfig,
    private val objectMapper: ObjectMapper
) {
    private val pem = Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", Pattern.DOTALL)
    val privateKey: PrivateKey = pemToPrivateKey(tokenConfig.secret)
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
    private fun pemToPrivateKey(pemData: String): PrivateKey {
        val m = pem.matcher(pemData.trim())
        require(m.matches()) { "$pemData is not PEM encoded data" }
        val type = m.group(1)
        val content = Base64.decode(m.group(2).toByteArray(StandardCharsets.UTF_8))
        return when (type) {
            "RSA PRIVATE KEY" -> {
                val seq = ASN1Sequence.getInstance(content)
                require(seq.size() == 9) { "Invalid RSA Private Key ASN1 sequence." }
                val key = RSAPrivateKey.getInstance(seq)
                val privateKeySpec = RSAPrivateCrtKeySpec(
                    key.modulus, key.publicExponent,
                    key.privateExponent, key.prime1, key.prime2, key.exponent1, key.exponent2,
                    key.coefficient
                )
                KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec)
            } else -> throw IllegalArgumentException("$type is not a supported format")
        }
    }
}