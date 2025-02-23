package com.slawicek.spring_jooq_experiments.helper

import org.springframework.http.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@RestControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {
    override fun createResponseEntity(
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        return if (body is ProblemDetail) {
            ResponseEntity(mapOf("title" to body.title, "error" to body.detail), headers, statusCode)
        } else {
            super.createResponseEntity(body, headers, statusCode, request)
        }
    }

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun exceptionHandler(e: Exception?): ResponseEntity<Any> {
        return ResponseEntity(mapOf("error" to "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}