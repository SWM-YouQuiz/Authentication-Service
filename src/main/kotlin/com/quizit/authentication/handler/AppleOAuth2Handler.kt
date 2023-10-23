package com.quizit.authentication.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.core.DefaultJwtProvider
import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.domain.AppleOAuth2UserInfo
import com.quizit.authentication.domain.OAuth2UserInfo
import com.quizit.authentication.domain.RefreshToken
import com.quizit.authentication.domain.enum.Provider
import com.quizit.authentication.dto.request.CreateUserRequest
import com.quizit.authentication.exception.UserNotFoundException
import com.quizit.authentication.global.annotation.Handler
import com.quizit.authentication.global.util.multiValueMapOf
import com.quizit.authentication.repository.TokenRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.commons.io.IOUtils
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.onErrorResume
import java.io.StringReader
import java.math.BigInteger
import java.net.URI
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Handler
class AppleOAuth2Handler(
    private val tokenRepository: TokenRepository,
    private val userClient: UserClient,
    private val webClient: WebClient,
    private val jwtProvider: DefaultJwtProvider,
    private val objectMapper: ObjectMapper,
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.apple.team-id}")
    private val teamId: String,
    @Value("\${spring.security.oauth2.client.registration.apple.key-id}")
    private val keyId: String,
    @Value("\${spring.security.oauth2.redirectUrl}")
    private val url: String
) {
    private val decoder = Base64.getUrlDecoder()

    fun redirect(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.formData()
            .flatMap {
                it["user"]?.firstOrNull()
                    ?.let { message ->
                        val user = objectMapper.readValue<Map<String, Any>>(message)
                        val name = (user["name"] as Map<String, String>).run { get("lastName") + get("firstName") }
                        val email = user["email"] as String

                        Mono.just(
                            AppleOAuth2UserInfo(
                                email = email,
                                name = name
                            )
                        )
                    } ?: run {
                    webClient.post()
                        .uri("https://appleid.apple.com/auth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .bodyValue(
                            multiValueMapOf(
                                "client_id" to clientId,
                                "client_secret" to createClientSecret(),
                                "code" to it["code"]!!.first(),
                                "redirect_uri" to "https://quizit.org/api/auth/oauth2/redirect/apple",
                                "grant_type" to "authorization_code"
                            )
                        )
                        .retrieve()
                        .bodyToMono<Map<String, Any>>()
                        .flatMap { body -> getOAuth2UserByToken(body["id_token"] as String) }
                }
            }
            .flatMap {
                onAuthenticationSuccess(it)
            }

    private fun onAuthenticationSuccess(oAuth2User: OAuth2UserInfo): Mono<ServerResponse> {
        var isSignUp = false

        return userClient.getUserByEmailAndProvider(oAuth2User.email, Provider.APPLE)
            .onErrorResume(UserNotFoundException::class) {
                isSignUp = true

                userClient.createUser(
                    CreateUserRequest(
                        email = oAuth2User.email,
                        username = oAuth2User.name!!,
                        image = "",
                        allowPush = true,
                        dailyTarget = 5,
                        provider = Provider.APPLE
                    )
                )
            }
            .flatMap {
                val jwtAuthentication = DefaultJwtAuthentication(
                    id = it.id,
                    authorities = listOf(SimpleGrantedAuthority(it.role.name))
                )
                val accessToken = jwtProvider.createAccessToken(jwtAuthentication)
                val refreshToken = jwtProvider.createRefreshToken(jwtAuthentication)

                tokenRepository.save(
                    RefreshToken(
                        userId = it.id,
                        content = refreshToken
                    )
                ).then(
                    ServerResponse.status(HttpStatus.FOUND)
                        .location(URI.create("$url?isSignUp=$isSignUp"))
                        .cookies {
                            mapOf(
                                "accessToken" to accessToken,
                                "refreshToken" to refreshToken,
                            ).map { cookie ->
                                it.set(
                                    cookie.key, ResponseCookie.from(cookie.key, cookie.value)
                                        .httpOnly(true)
                                        .secure(true)
                                        .maxAge(Duration.ofMinutes(10))
                                        .path("https://quizit.org/")
                                        .build()
                                )
                            }
                        }
                        .build()
                )
            }
    }

    private fun getOAuth2UserByToken(token: String): Mono<AppleOAuth2UserInfo> =
        webClient.get()
            .uri("https://appleid.apple.com/auth/keys")
            .retrieve()
            .bodyToMono<Map<String, List<Map<String, String>>>>()
            .map {
                Jwts.parserBuilder()
                    .setSigningKey(createPublicKey(token, it["keys"]!!))
                    .build()
                    .parseClaimsJws(token)
                    .body
            }
            .map {
                AppleOAuth2UserInfo(
                    email = it["email"] as String,
                    name = null
                )
            }

    private fun createPublicKey(token: String, keys: List<Map<String, String>>): PublicKey {
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

    private fun createClientSecret(): String {
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
}