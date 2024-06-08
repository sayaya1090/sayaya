package net.sayaya.github

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SecureDigestAlgorithm
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.RSAPrivateCrtKeySpec
import java.util.regex.Pattern
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.util.encoders.Base64

class JwtTokenFactory {
    companion object {
        fun generate(payload: String, algorithm: String, privateKey: String): String = generate(payload, algorithm, parse(privateKey))
        private fun generate(payload: String, algorithm: String, privateKey: PrivateKey): String {
            val signatureAlgorithm = Jwts.SIG.get()[algorithm]
            if(signatureAlgorithm is SecureDigestAlgorithm) {
                return Jwts.builder()
                    .header().add("typ", "JWT").and()
                    .content(payload)
                    .signWith(privateKey, signatureAlgorithm as SecureDigestAlgorithm<PrivateKey, *>)
                    .compact()
            } else throw IllegalArgumentException("Unsupported algorithm: $algorithm")
        }
        private val pem = Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", Pattern.DOTALL)
        private fun parse(pemData: String): PrivateKey {
            val m = pem.matcher(pemData.trim())
            require(m.matches()) { "String is not PEM encoded data" }
            val type = m.group(1)
            val content = Base64.decode(m.group(2).toByteArray(StandardCharsets.UTF_8))
            val fact = KeyFactory.getInstance("RSA")
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
                    fact.generatePrivate(privateKeySpec)
                } else -> throw IllegalArgumentException("$type is not a supported format")
            }
        }
    }
}