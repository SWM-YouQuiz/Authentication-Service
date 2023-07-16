package com.youquiz.authentication.exception

import com.youquiz.authentication.global.exception.ServerException

class UserNotFoundException(
    override val message: String = "유저를 찾을 수 없습니다."
) : ServerException(code = 404, message)