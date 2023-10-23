package com.quizit.authentication.exception

import com.quizit.authentication.global.exception.ServerException

data class UserAlreadyExistException(
    override val message: String = "이미 존재하는 계정입니다."
) : ServerException(code = 409, message)