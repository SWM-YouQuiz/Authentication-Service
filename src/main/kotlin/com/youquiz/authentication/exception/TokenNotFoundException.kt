package com.youquiz.authentication.exception

import com.youquiz.authentication.global.exception.ServerException

class TokenNotFoundException(
    override val message: String = "토큰을 찾을 수 없습니다."
) : ServerException(code = 404, message)