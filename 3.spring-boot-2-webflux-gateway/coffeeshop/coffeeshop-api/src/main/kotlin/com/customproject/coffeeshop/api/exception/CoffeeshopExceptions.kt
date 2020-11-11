package com.customproject.coffeeshop.api.exception

import org.springframework.http.HttpStatus


abstract class CoffeeshopException(val webStatus: HttpStatus, val errorStatus: String, message: String?, cause: Throwable?)
    : Exception("[${webStatus.value()}_$errorStatus]" + message, cause) {

    constructor(webStatus: HttpStatus, errorStatus: String) : this(webStatus, errorStatus, "")
    constructor(webStatus: HttpStatus, errorStatus: String, message: String) : this(webStatus, errorStatus, message, null)
}

// 400
class InvalidValueException(msg: String, cause: Throwable?) : CoffeeshopException(HttpStatus.BAD_REQUEST, "0", msg, cause) {
    constructor(msg: String) : this(msg, null)
}

// 401
class UnauthorizedException(msg: String, cause: Throwable?) : CoffeeshopException(HttpStatus.UNAUTHORIZED, "1", msg, cause) {
    constructor(msg: String) : this(msg, null)
}

// 403
class ForbiddenUserException(msg: String, cause: Throwable?) : CoffeeshopException(HttpStatus.FORBIDDEN, "1", msg, cause) {
    constructor(msg: String) : this(msg, null)
}

// 404
class NotFoundException(msg: String, cause: Throwable?) : CoffeeshopException(HttpStatus.NOT_FOUND, "0", msg, cause) {
    constructor(msg: String) : this(msg, null)
}
class HandlerNotFoundException(msg: String, cause: Throwable?) : CoffeeshopException(HttpStatus.NOT_FOUND, "1", msg, cause) {
    constructor(msg: String) : this(msg, null)
}

// 500
class UnexpectedException(msg: String, cause: Throwable?) : CoffeeshopException(HttpStatus.INTERNAL_SERVER_ERROR, "0", msg, cause) {
    constructor(msg: String) : this(msg, null)
}
