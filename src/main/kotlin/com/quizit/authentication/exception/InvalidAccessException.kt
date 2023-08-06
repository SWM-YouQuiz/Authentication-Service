package com.quizit.authentication.exception

import com.quizit.authentication.global.exception.ServerException

class InvalidAccessException(
    override val message: String = "유효하지 않은 접근입니다."
) : ServerException(code = 403, message)