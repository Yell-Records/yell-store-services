package com.yellrecords.services.logging

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import java.lang.Exception
import java.util.UUID

@Component
class RequestLoggingInterceptor : HandlerInterceptor {
    private val log = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        request.setAttribute("startTime", System.currentTimeMillis())

        val requestId = UUID.randomUUID().toString().substring(0, 8)
        request.setAttribute("requestId", requestId)

        log.info("-> [REQUEST:  $requestId] ${request.asLoggerString()}")
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val start = request.getAttribute("startTime") as Long
        val duration = System.currentTimeMillis() - start

        val requestId = request.getAttribute("requestId") as String

        val isUnhandledError = HttpStatus.valueOf(response.status).is5xxServerError

        if (ex != null && isUnhandledError) {
            log.error("Unhandled exception occurred during request [$requestId]", ex)
        }

        val loggedResponse = response.asLoggerString(request.requestURI, duration)

        log.info("<- [RESPONSE: $requestId] $loggedResponse")
    }

    companion object {
        /**
         * Builds a stringified version of this request for logging.
         *
         * @return Stringified request information
         */
        private fun HttpServletRequest.asLoggerString(): String {
            val builder = StringBuilder()

            // Service
            val serviceName = this.requestURI.removePrefix("/api/").substringBefore("/")
            if (serviceName.isNotEmpty()) {
                builder.appendSpaced("service=\"$serviceName\"")
            } else {
                builder.appendSpaced("service=null")
            }

            // HTTP method
            builder.appendSpaced("method=\"${this.method}\"")

            // URI template
            val template =
                this.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) as? String

            template?.let { builder.appendSpaced("route=\"$it\"") }

            // URI
            builder.appendSpaced("uri=\"${this.requestURI}\"")

            return builder.toString()
        }

        /**
         * Builds a stringified version of this response for logging.
         *
         * @param requestUri Request URI
         * @param duration Time taken to complete
         * @return Stringified response information
         */
        private fun HttpServletResponse.asLoggerString(
            requestUri: String,
            duration: Long,
        ): String {
            val builder = StringBuilder()

            // Service
            val serviceName = requestUri.removePrefix("/api/").substringBefore("/")
            if (serviceName.isNotEmpty()) {
                builder.appendSpaced("service=\"$serviceName\"")
            } else {
                builder.appendSpaced("service=null")
            }

            // Duration
            builder.appendSpaced("duration=${duration}ms")

            // HTTP status
            val statusName = HttpStatus.valueOf(this.status).reasonPhrase
            builder.appendSpaced("status=${this.status}")
            builder.appendSpaced("statusName=\"$statusName\"")

            return builder.toString()
        }

        /** Appends a string with a space at the end. */
        private fun StringBuilder.appendSpaced(str: String) = this.append(str).append(" ")
    }
}
