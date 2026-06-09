package com.yellrecords.services.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(
        ex: NotFoundException,
        req: HttpServletRequest,
    ): ErrorResponse {
        req.setAttribute("errorMessage", ex.message)
        return SimpleErrorResponse(HttpStatus.NOT_FOUND, ex.message)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(
        ex: ForbiddenException,
        req: HttpServletRequest,
    ): ErrorResponse {
        req.setAttribute("errorMessage", ex.message)
        return SimpleErrorResponse(HttpStatus.FORBIDDEN, ex.message)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(
        ex: BadRequestException,
        req: HttpServletRequest,
    ): ErrorResponse {
        req.setAttribute("errorMessage", ex.message)
        return SimpleErrorResponse(HttpStatus.BAD_REQUEST, ex.message)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(
        ex: ConflictException,
        req: HttpServletRequest,
    ): ErrorResponse {
        req.setAttribute("errorMessage", ex.message)
        return SimpleErrorResponse(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(): ErrorResponse = SimpleErrorResponse(HttpStatus.NOT_FOUND, "Not Found")

    @ExceptionHandler(Exception::class)
    fun handleUnhandled(): ErrorResponse = SimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
}
