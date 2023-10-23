package com.quizit.authentication.service

import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.domain.GoogleOAuth2UserInfo
import com.quizit.authentication.domain.KakaoOAuth2UserInfo
import com.quizit.authentication.domain.enum.Provider
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OAuth2Service(
    private val userClient: UserClient,
) : ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(request: OAuth2UserRequest): Mono<OAuth2User> {
        val oAuth2User = DefaultReactiveOAuth2UserService()
            .loadUser(request)
        val provider = Provider.valueOf(request.clientRegistration.registrationId.uppercase())

        return oAuth2User.flatMap {
            when (provider) {
                Provider.GOOGLE -> Mono.just(GoogleOAuth2UserInfo(it.attributes))
                Provider.KAKAO -> Mono.just(KakaoOAuth2UserInfo(it.attributes))
                Provider.APPLE -> Mono.error(UnsupportedOperationException())
            }
        }
    }
}