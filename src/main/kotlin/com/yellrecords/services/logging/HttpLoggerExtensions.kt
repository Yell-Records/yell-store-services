package com.yellrecords.services.logging

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.HandlerMapping

/**
 * Builds a stringified version of this request for logging. If the service name is unrecognized,
 * returns null.
 *
 * @return Stringified request information
 */
fun HttpServletRequest.asInfoLog(): String {
    val builder = StringBuilder()

    // Service
    extractServiceName(this.requestURI)?.let { serviceName ->
        builder.appendSpaced("service=\"$serviceName\"")
    }

    builder.appendSpaced("method=\"${this.method}\"")

    // URI template
    val template = this.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) as? String
    template?.let { builder.appendSpaced("route=\"$it\"") }

    // URI
    builder.appendSpaced("uri=\"${this.requestURI}\"")

    builder.appendSpaced("ip=${this.clientIp()}")
    builder.appendSpaced("ua=${this.userAgent()}")

    return builder.toString()
}

/**
 * Gets the IP associated with this request.
 *
 * @return Client's IP
 */
fun HttpServletRequest.clientIp(): String {
    val header = this.getHeader("X-Forwarded-For")

    return header?.substringBefore(",")?.trim() ?: this.remoteAddr
}

/**
 * Gets the user agent associated with this request.
 *
 * @return User agent
 */
fun HttpServletRequest.userAgent(): String =
    this
        .getHeader("User-Agent")
        ?.filter { it >= ' ' }
        ?.take(80)
        .orEmpty()

/**
 * Builds a WARN level log message for requests attempting to reach an unknown API route.
 *
 * @return Stringified request information
 */
fun HttpServletRequest.asWarnUnknownLog(): String {
    val builder = StringBuilder()

    builder.appendSpaced("(Unrecognized API route)")
    builder.appendSpaced("method=\"${this.method}\"")
    builder.appendSpaced("uri=\"${this.requestURI}\"")
    builder.appendSpaced("ip=${this.clientIp()}")
    builder.appendSpaced("ua=${this.userAgent()}")

    return builder.toString()
}

/** Checks if the destination on this request is to a valid endpoint service. */
fun HttpServletRequest.isToKnownService(): Boolean = extractServiceName(this.requestURI) != null

/**
 * Builds a stringified version of this response for logging. If the service name is unrecognized,
 * returns null.
 *
 * @param origin Request associated with this response
 * @param duration Time taken to complete
 * @return Stringified response information
 */
fun HttpServletResponse.asInfoLog(
    origin: HttpServletRequest,
    duration: Long,
): String {
    val builder = StringBuilder()

    // Service
    extractServiceName(origin.requestURI)?.let { serviceName ->
        builder.appendSpaced("service=\"$serviceName\"")
    } ?: builder.appendSpaced("service=null")

    // Duration
    builder.appendSpaced("duration=${duration}ms")

    // HTTP status
    val statusName = HttpStatus.valueOf(this.status).reasonPhrase
    builder.appendSpaced("status=${this.status}")
    builder.appendSpaced("statusName=\"$statusName\"")

    val isHandledError = HttpStatus.valueOf(this.status).is4xxClientError
    val errorMsg = origin.getAttribute("errorMessage")
    if (isHandledError && errorMsg != null) {
        builder.appendSpaced("msg=\"$errorMsg\"")
    }

    // Device and user info
    builder.appendSpaced("ip=${origin.clientIp()}")
    builder.appendSpaced("ua=${origin.userAgent()}")

    return builder.toString()
}

/** Gets the service being queried. If the service is to an unknown controller, returns null. */
private fun extractServiceName(requestURI: String): String? {
    val service = requestURI.removePrefix("/api/").substringBefore("/")

    return if (service in AllowedServices.names) service else null
}

/** Appends a string with a space at the end. */
private fun StringBuilder.appendSpaced(str: String) = this.append(str).append(' ')
