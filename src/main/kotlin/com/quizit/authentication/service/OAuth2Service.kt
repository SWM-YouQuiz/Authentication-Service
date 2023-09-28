//package com.quizit.authentication.service
//
//import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
//import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
//import org.springframework.security.oauth2.core.user.OAuth2User
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class OAuth2Service : ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
//    override fun loadUser(request: OAuth2UserRequest): Mono<OAuth2User> {
//        val oAuth2User = DefaultReactiveOAuth2UserService().loadUser(request)
//        val provider = request.clientRegistration.registrationId
//
//    }
//}