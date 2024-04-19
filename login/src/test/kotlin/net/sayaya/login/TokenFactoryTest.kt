package net.sayaya.login

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.sayaya.JsonConfig
import net.sayaya.TokenConfig
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.pkcs.RSAPublicKey
import org.bouncycastle.util.encoders.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*
import java.util.regex.Pattern

internal class TokenFactoryTest: BehaviorSpec({
    val om = JsonConfig().objectMapper()
    val user = User(UUID.randomUUID())

    Given("지원하는 알고리즘으로 토큰 팩토리 생성 후에") {
        val config = TokenConfig().apply {
            signatureAlgorithm = "RS256"
            publisher = "test-publisher"
            client = "test-client"
            duration = 1000L
            secret = PRIVATE_KEY
        }
        val keyPair = KeyPair(config)
        val factory = TokenFactory(config, om, keyPair)
        When("사용자 정보로 토큰 생성을 요청하면") {
            val token = factory.publish(user)
            Then("JWT 토큰이 발급된다") {
                token shouldNotBe null
            }
            Then("JWT 토큰을 decrypt 할 수 있고 그 값으로 원래 사용자의 정보를 확인할 수 있다") {
                val decrypt = jwtParser(PUBLIC_KEY).parseSignedClaims(token)
                val claims = decrypt.payload

                user.id.toString() shouldBeEqual claims["name"]!!
                claims["authorities"] shouldNotBe null
                val authorities = claims["authorities"].shouldBeInstanceOf<List<String>>()
                authorities shouldContain "ROLE_USER"
                claims.issuer shouldBeEqual config.publisher
                claims.audience shouldContain config.client
                claims.notBefore shouldNotBe null
            }
        }
    }
    Given("지원되지 않는 알고리즘으로 토큰 팩토리 생성 후에") {
        val config = TokenConfig().apply {
            signatureAlgorithm = "Invalid Algorithm"
            publisher = "test-publisher"
            client = "test-client"
            duration = 1000L
            secret = PRIVATE_KEY
        }
        val keyPair = KeyPair(config)
        val factory = TokenFactory(config, om, keyPair)
        When("사용자 정보로 토큰 생성을 요청하면") {
            Then("IllegalArgumentException 예외를 발생시킨다") {
                shouldThrow<IllegalArgumentException> {
                    factory.publish(user)
                }
            }
        }
    }
    Given("잘못된 키페어가 주어지면") {
        val config = TokenConfig().apply {
            signatureAlgorithm = "RS256"
            publisher = "test-publisher"
            client = "test-client"
            duration = 1000L
            secret = ILLEGAL_PRIVATE_KEY
        }
        Then("IllegalArgumentException 예외를 발생시킨다") {
            shouldThrow<IllegalArgumentException> {
                val keyPair = KeyPair(config)
                val factory = TokenFactory(config, om, keyPair)
                factory.publish(user)
            }
        }
    }
}) {
    companion object {
        val PRIVATE_KEY =
            """
            -----BEGIN RSA PRIVATE KEY-----
            MIIEowIBAAKCAQEAv0q4ej+Hf7LulM4hMwaJZ86APEOLEPqm2FNaToI4zV7z/pVi
            MC6iPzf4zHNjF6JWrm3S5cngEmw3I9ElcoQrd8/ndRveZqOkNF8uch7x0cOJC6ox
            GIxvtmO1Ec/J8HhYPmr+it1Z2Rh5YQqa7NfA4AR/TmoJd/pV1X/pCwF0InlUCIHI
            AOTTzcJBnT3XuFmsKPWCPdNjC+YRtRULX/q656weT0POfV13jbmz6UoQwF6PL6pK
            tBfvtRzd1eA+Pc47vJaHTy3hLPmpVU9UuRFJ1C5u6QeyKP21J23rygevjpnlVQOe
            e3WQv+v5pPwsagGfkAAIbebLrMRm5X5NwLXAOQIDAQABAoIBAQCBdw3B1ytalvx4
            A8ZeZWcrtYv+vWvqcunm8QrmpaXSARi7zdilaXpvtO8TWGjRfxKRfUzGLsoTTeBH
            wm5IwgE1VqV9Ef6Eku44b87cd+sMH/2pwmb2CV42H+dVhb9Tm++FVx6tV0BO+Qx1
            TBssfp1QQFr09DkyVWAwXiCYTUSPa0Y7mOoyp0p8q96tCH7gKYUpXO5mdTuNjRVn
            lIfHPWjxpiAFH2VVOj8jqMLrzyLnixAiouemAzCBs83aHuDO0/gRO/iN104L9n/o
            QIPOvecFI3+MqU0JzJYSNEz0Zi+o1uTgZc6gUicPVeFH9cBTQPd6EKgVO8LOPaeH
            2e4RYYttAoGBAPrGsO2DQ2zWy7lxPoZMRm5xwiCogwoWMiQ7ubm9SZ7TaTQzreIY
            ISR0gzpM2KwD0QTWXoPO6ZpuwqO8FGcNSGosce/8nE+ZPb7CYBBfobFHo2YkM++Y
            elNsKTz9QAGXCbk5zF/ROnuTniAKhBDcZUZ4zICSwtTTKyDmFNXScFMjAoGBAMNG
            0agrp0Voo4FWva2RZ1fbH2mqw2eTIyWcWDkl20I8Jgv5Yuw8a9QikbQgT/6sUsOf
            TjMJ2VfTlQNwL/YRwuT6Q/GY8tIu2Uzjm5tppmqDrMbkBgzCDxWfV9OLM7xDYDwh
            5TfsSE2jxWfMswi/zIxfCjGKyfkW4Qt2/0hjXjLzAoGATI122Spm3MS9MADX21tR
            bMmhPyLxzZR0/gaVbZPQ84EJ7nuQKyK+i0hd/uASjIAlwFpIQ+hX+2KwXBdACy1M
            28xxg5cTiGD5LlBbzuPCkkGSKc4HZK6hOPIdrJaKgXG/8CEquF1AgxTPAmzzX8pH
            yDl8BAvJGfrUgZh658Lzsw0CgYBUUV3x6Xd+huIi1Ntt+JzQ2LLFo5BgRq4kbU/C
            zU/RV7tt7C8EpkpA/PRA/LrN0oaiJUVU0GnifF+ZbnWnIKAw0sdHqK0giE4X3yev
            gXzz/Qs7jfX2yExPH9CCbVbXcZg6HsCk1weZTp/MZBziKD8gVWFHZxAy1+gwVR+B
            mDZydQKBgANuScImHgEfdN3VuC8qnA9p43LdCMLNBhHyYnvnBS172w5w+v+gSTyD
            RJeIvupO6Mxa4c0npg9aI8Ksb4D9rhCyHft6+pk+k4pFF31GqypQq1Ogqc6QF1DL
            jwfHWb/mT84fvow0oR6Yqg7bzmpXIIqZ+m7em3SaHXIc6cMFoodO
            -----END RSA PRIVATE KEY-----
        """.trimIndent()
        val PUBLIC_KEY =
            """
            -----BEGIN RSA PUBLIC KEY-----
            MIIBCgKCAQEAv0q4ej+Hf7LulM4hMwaJZ86APEOLEPqm2FNaToI4zV7z/pViMC6i
            Pzf4zHNjF6JWrm3S5cngEmw3I9ElcoQrd8/ndRveZqOkNF8uch7x0cOJC6oxGIxv
            tmO1Ec/J8HhYPmr+it1Z2Rh5YQqa7NfA4AR/TmoJd/pV1X/pCwF0InlUCIHIAOTT
            zcJBnT3XuFmsKPWCPdNjC+YRtRULX/q656weT0POfV13jbmz6UoQwF6PL6pKtBfv
            tRzd1eA+Pc47vJaHTy3hLPmpVU9UuRFJ1C5u6QeyKP21J23rygevjpnlVQOee3WQ
            v+v5pPwsagGfkAAIbebLrMRm5X5NwLXAOQIDAQAB
            -----END RSA PUBLIC KEY-----      
        """.trimIndent()
        fun jwtParser(publicKey: String): JwtParser = Jwts.parser().verifyWith(pemToPublicKey(publicKey)).build()
        private val pem = Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", Pattern.DOTALL)
        private fun pemToPublicKey(pemData: String): PublicKey {
            val m = pem.matcher(pemData.trim())
            require(m.matches()) { "$pemData is not PEM encoded data" }
            val type = m.group(1)
            val content = Base64.decode(m.group(2).toByteArray(StandardCharsets.UTF_8))
            return when (type) {
                "RSA PUBLIC KEY" -> {
                    val seq = ASN1Sequence.getInstance(content)
                    require(seq.size() == 2) { "Invalid RSA Public Key ASN1 sequence." }
                    val key = RSAPublicKey.getInstance(seq)
                    val pubSpec = RSAPublicKeySpec(key.modulus, key.publicExponent)
                    KeyFactory.getInstance("RSA").generatePublic(pubSpec)
                } else -> throw IllegalArgumentException("$type is not a supported format")
            }
        }
        val ILLEGAL_PRIVATE_KEY = """
            -----BEGIN PRIVATE KEY-----
            MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDN7kQTaj62h/Py
            iKGEP3x50jApcbVowYCOzKBqMDl465IsnCQmsNpvDWL412M+gL/LnbzAWlNtdkGi
            C1QvDFk4Fqzp3fSXxepqT8jci5Rm9khaBDrXa0rqmiSUl4//acGPdr9ERAZLbzep
            fWUCzxC+u5dXAqBc40kq26Ngu2oOgQ4oRaWiJxuQLU9QvpeoIWmYUrdyGh7Xd5Oy
            OfIQCIO54eM6rV0g7/8C+j4vIiNBL+7Ec7V/Nj2WnHqVjd+aZUyGcAiyI1oVa4P0
            ty2Z8Wjt6OOWGMV10Zyd3FkZJHpvchtT3C3fMyN1Vfodhcfm2wD3om5edZwioK7V
            pQ1Vw/atAgMBAAECggEAHjEfMPuEO02+ceIfUBzgupma7xCZSMUJ/KW6pSbx3i0n
            jrvLvxJxzBNZ3eLyTYEh3ZAAUvFLRwKuB7yCeK1mTUdhzZ35lODzB+gvqkoalWfp
            Xq09FUVJRBEaaOC5v3g5ZTfDPhZ9F8U0E3jrfNhv+47UVvDvIv89xHdUQkPn/j0h
            P1T2O9rqLOuuTfc7b8CW7Si4iOU6aDxCAzTN8VB+uoOldYy2ohlEAAqcZzbGNvBp
            BZENRGYR1xhP272EOpCElH4t/wyxkHGYOjsHk3XGkeZMgt5i3wQjg3W6L9sAnlXV
            lUrVoRQkSdTFGhrtzTnR/w/+IOcJ925SeeqsYU9r4QKBgQDdQiqCw2o8NQyWW9O8
            4cM5fbKDuBK4O0Aha1RLsmm0Sfvn9+yiDw3DEsBO3y6Eqpy7R3V/GSiNS8R5aLjY
            BUCLbl8giLXE1VinIi/QdOzm5KbsV376fOXVhUYCvu4KO1ByQbavRIx1ZyDznOVz
            nghLJ/lzkDjQ1oIM1nXwVhC2kQKBgQDuQ/owt01UlwMQD/WKg1VPnbuBjEoAHYLT
            jUDD5XmdjDXAZqZZZr6fftEs+30FDUDVyQD2DpNFShSUMLLfv8Cqa6Nxr/Gi6h5v
            N6i2eqcEP+ElsTjVEprLmJnPcWaAyxQ/cOeeO1QJYBfTjQce5qPdoeM3ZQ85gdne
            lxJ9ntFkXQKBgCWpBHlbb7KtyOWy3ZhxyPv5JDB9XVpUY3cMwL1dsdZJHYJ/VZzD
            +TCx+V+zZ0SXkbPi1uH6U0exlNChfYzvX+wbrj9oZaAKPFu+Wusda9FlK/BgeDGE
            ns44MBF5lCTTsb6w5rpwbYPyOM6ebwO962V3eWMtMi6BxIMQ4eQ1kgSxAoGAHh/e
            HBaw4kcNjhRWeSLNwHky4dg46vbQOascSyhSTgcSLLdNxPPmmgfQrp1FW0QntF/r
            jTCekh4hp/LYSs3VsDZXveH+7RgorOsem6O6RiFFeByGEPp/M8qhd8bgrNwAPV0r
            C6h4oY7StF6aTd/fTABR2Akg+aZfQ3NaVSOlWckCgYB/0d5d0ZaW4K46W7IEvo1F
            O6DoFB9Bx4VyHsfQgZlPDEyqL+1opPk61ch1hmXK4dGkDE7qAVZVdad7P7ErCC6n
            OkAPYSifCb0wwKfeuKvYNHrQubBoYNsmtVWHECs8KJGxWYSiNKGEdEZrLK0H5ugR
            zmQuLNMejmdRN/ucvMUw8g==
            -----END PRIVATE KEY-----
        """.trimIndent()
    }
}