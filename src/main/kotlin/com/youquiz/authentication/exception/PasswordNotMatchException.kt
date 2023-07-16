package com.youquiz.authentication.exception

import com.youquiz.authentication.global.exception.ServerException

class PasswordNotMatchException(
    override val message: String = "패스워드가 일치하지 않습니다."
) : ServerException(code = 400, message)