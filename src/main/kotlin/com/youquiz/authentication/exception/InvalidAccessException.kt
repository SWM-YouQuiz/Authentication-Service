package com.youquiz.authentication.exception

import com.youquiz.authentication.global.exception.ServerException

class InvalidAccessException(
    override val message: String = "유효하지 않은 접근입니다."
) : ServerException(code = 403, message)