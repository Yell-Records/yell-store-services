package com.yellrecords.services.logging

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.lang.Exception
import java.util.UUID

@Component
class RequestLoggingInterceptor : HandlerInterceptor {
    companion object {
        private const val ATTR_REQUEST_ID = "requestId"
        private const val ATTR_START_TIME = "startTime"
    }

    private val logger = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        request.setAttribute(ATTR_START_TIME, System.currentTimeMillis())

        val requestId = UUID.randomUUID().toString().substring(0, 8)
        request.setAttribute(ATTR_REQUEST_ID, requestId)

        if (request.isToKnownService()) {
            logger.info("-> [REQUEST:  $requestId] ${request.asInfoLog()}")
        } else {
            logger.warn("-> [REQUEST:  $requestId] ${request.asWarnUnknownLog()}")
        }

        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val start = request.getAttribute(ATTR_START_TIME) as Long
        val duration = System.currentTimeMillis() - start

        val requestId = request.getAttribute(ATTR_REQUEST_ID) as String

        val isUnhandledError = HttpStatus.valueOf(response.status).is5xxServerError

        if (ex != null && isUnhandledError) {
            logger.error("Unhandled exception occurred during request [$requestId]", ex)
        }

        val loggedResponse = response.asInfoLog(request, duration)

        logger.info("<- [RESPONSE: $requestId] $loggedResponse")
    }
}
