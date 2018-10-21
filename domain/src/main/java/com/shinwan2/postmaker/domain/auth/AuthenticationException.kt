package com.shinwan2.postmaker.domain.auth

class AuthenticationException(
    message: String,
    innerException: Throwable
): RuntimeException(message, innerException) {
    constructor(innerException: Throwable): this(innerException.message ?: "", innerException)
}