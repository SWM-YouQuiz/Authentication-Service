package com.quizit.authentication.exception

import com.quizit.authentication.global.exception.ServerException

class OAuthLoginException(
    override val message: String = "소셜 로그인 유저는 일반 로그인을 이용할 수 없습니다."
) : ServerException(code = 400, message)