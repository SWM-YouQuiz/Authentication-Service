//package com.quizit.authentication.domain
//
//import com.quizit.authentication.domain.enum.Provider
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.oauth2.core.oidc.OidcIdToken
//import org.springframework.security.oauth2.core.oidc.OidcUserInfo
//import org.springframework.security.oauth2.core.oidc.user.OidcUser
//
//sealed class OAuth2UserInfo(
//    val email: String,
//    val provider: Provider,
//    open val attributes: Map<String, *>
//) : OidcUser {
//    override fun getAttributes(): Map<String, *> = attributes
//
//    override fun getClaims(): Map<String, *>? = null
//
//    override fun getUserInfo(): OidcUserInfo? = null
//
//    override fun getIdToken(): OidcIdToken? = null
//
//    override fun getName(): String? = null
//
//    override fun getAuthorities(): List<GrantedAuthority>? = null
//}
//
//class GoogleOAuth2UserInfo(
//    override val attributes: Map<String, *>
//) : OAuth2UserInfo(
//    email = attributes["email"] as String,
//    provider = Provider.GOOGLE,
//    attributes = attributes
//)
//
//class KakaoOAuth2UserInfo(
//    override val attributes: Map<String, *>
//) : OAuth2UserInfo(
//    email = (attributes["properties"] as Map<*, *>)["email"] as String,
//    provider = Provider.KAKAO,
//    attributes = attributes
//)
//
//class AppleOAuth2UserInfo(
//    override val attributes: Map<String, *>
//) : OAuth2UserInfo(
//    email = (attributes["response"] as Map<*, *>)["email"] as String,
//    provider = Provider.APPLE,
//    attributes = attributes
//)