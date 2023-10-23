package com.quizit.authentication.domain

import com.quizit.authentication.domain.enum.Provider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

sealed class OAuth2UserInfo(
    val email: String,
    val provider: Provider,
    private val name: String?,
    private val attributes: Map<String, *>
) : OAuth2User {
    override fun getAttributes(): Map<String, *> = attributes

    override fun getName(): String? = name

    override fun getAuthorities(): List<GrantedAuthority>? = null
}

class GoogleOAuth2UserInfo(
    private val attributes: Map<String, *>
) : OAuth2UserInfo(
    email = attributes["email"] as String,
    provider = Provider.GOOGLE,
    name = attributes["name"] as String,
    attributes = attributes
)

class AppleOAuth2UserInfo(
    attributes: Map<String, *>
) : OAuth2UserInfo(
    email = (attributes["user"] as Map<*, *>)["email"] as String,
    provider = Provider.APPLE,
    name = ((attributes["user"] as Map<*, *>)["name"]) as String,
    attributes = attributes
)

class KakaoOAuth2UserInfo(
    private val attributes: Map<String, *>
) : OAuth2UserInfo(
    email = (attributes["kakao_account"] as Map<*, *>)["email"] as String,
    provider = Provider.KAKAO,
    name = (attributes["properties"] as Map<*, *>)["nickname"] as String,
    attributes = attributes
)