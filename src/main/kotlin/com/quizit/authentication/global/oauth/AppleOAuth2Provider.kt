package com.quizit.authentication.global.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.commons.io.IOUtils
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.StringReader
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
class AppleOAuth2Provider(
    private val objectMapper: ObjectMapper,
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.apple.team-id}")
    val teamId: String,
    @Value("\${spring.security.oauth2.client.registration.apple.key-id}")
    val keyId: String,
) {
    private val decoder = Base64.getUrlDecoder()

    fun createClientSecret(): String {
        val resource = ClassPathResource("static/private_key.p8")
        val pemParser = PEMParser(StringReader(IOUtils.toString(resource.inputStream, StandardCharsets.UTF_8)))
        val privateKey = JcaPEMKeyConverter()
            .getPrivateKey(pemParser.readObject() as PrivateKeyInfo)

        return Jwts.builder()
            .setHeaderParams(
                mapOf(
                    "alg" to "ES256",
                    "kid" to keyId,
                    "typ" to "JWT"
                )
            )
            .setIssuer(teamId)
            .setAudience("https://appleid.apple.com")
            .setSubject(clientId)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact()
    }

    fun createPublicKey(token: String, keys: List<Map<String, String>>): PublicKey {
        val encodedHeader = token.split(".").first()
        val decodedHeader = String(decoder.decode(encodedHeader))
        val headers = objectMapper.readValue<Map<String, String>>(decodedHeader)

        return keys.first { (it["alg"] == headers["alg"]) && (it["kid"] == headers["kid"]) }
            .let {
                val n = decoder.decode(it["n"])
                val e = decoder.decode(it["e"])
                val publicKeySpec = RSAPublicKeySpec(BigInteger(1, n), BigInteger(1, e))
                val keyFactory = KeyFactory.getInstance(it["kty"])

                keyFactory.generatePublic(publicKeySpec)
            }
    }
}