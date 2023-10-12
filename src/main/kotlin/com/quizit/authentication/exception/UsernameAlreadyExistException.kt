package com.quizit.authentication.exception

import com.quizit.authentication.global.exception.ServerException

class UsernameAlreadyExistException(
    override val message: String = "이미 존재하는 아이디입니다."
) : ServerException(code = 409, message)