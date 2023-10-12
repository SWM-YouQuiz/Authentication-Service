package com.quizit.authentication.service

import com.quizit.authentication.adapter.client.UserClient
import com.quizit.authentication.domain.AppleOAuth2UserInfo
import com.quizit.authentication.domain.GoogleOAuth2UserInfo
import com.quizit.authentication.domain.KakaoOAuth2UserInfo
import com.quizit.authentication.domain.enum.Provider
import com.quizit.authentication.dto.request.CreateUserRequest
import com.quizit.authentication.exception.UserNotFoundException
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

        return oAuth2User.map {
            when (provider) {
                Provider.GOOGLE -> GoogleOAuth2UserInfo(it.attributes)
                Provider.APPLE -> AppleOAuth2UserInfo(it.attributes)
                Provider.KAKAO -> KakaoOAuth2UserInfo(it.attributes)
            }
        }.flatMap {
            userClient.getUserByUsername(it.email)
                .onErrorResume(UserNotFoundException::class.java) { _ ->
                    userClient.createUser(
                        CreateUserRequest(
                            username = it.email,
                            password = null,
                            nickname = it.name,
                            image = null,
                            allowPush = true,
                            dailyTarget = 5,
                            provider = it.provider
                        )
                    )
                }
                .thenReturn(it)
        }
    }
}